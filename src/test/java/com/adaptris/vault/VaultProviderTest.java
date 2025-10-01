package com.adaptris.vault;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.vault.authentication.TokenAuthentication;
import org.springframework.vault.client.VaultEndpoint;
import org.springframework.vault.core.VaultKeyValueOperations;
import org.springframework.vault.core.VaultKeyValueOperationsSupport;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.support.VaultMount;
import org.springframework.vault.support.VaultResponseSupport;

import java.io.File;
import java.net.InetSocketAddress;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class VaultProviderTest extends AbstractVaultTest{
    @BeforeEach
    public void setUp() {
        HashicorpVaultProvider.deletePropertiesFile(true);
    }

    @Test
    public void testSecretDecode() {
        InetSocketAddress address = getHostAddressForService(VAULT_SERVICE_NAME, VAULT_PORT);
        String endpointAddress = String.format("http://%s:%d", address.getHostName(), address.getPort());

        String token = VAULT_ROOT_TOKEN;
        VaultEndpoint endpoint = new VaultEndpoint();
        endpoint.setScheme("http");
        endpoint.setPort(address.getPort());
        VaultTemplate vaultTemplate = new VaultTemplate(endpoint,
                new TokenAuthentication(token));

        // secret to store
        Secrets secrets = new Secrets();
        secrets.username = "hello";
        secrets.password = "world";

        // create a key value mount if not exists
        Map<String, VaultMount> mounts = vaultTemplate.opsForSys().getMounts();
        if (!mounts.containsKey("dev-secrets/")) {
            vaultTemplate.opsForSys().mount("dev-secrets", VaultMount.builder().type("kv-v2").build());
        }

        VaultKeyValueOperations keyValueOperations = vaultTemplate.opsForKeyValue("dev-secrets",
                VaultKeyValueOperationsSupport.KeyValueBackend.KV_2);
        keyValueOperations.put("myapp", secrets);

        // confirm that it was created
        VaultResponseSupport<Secrets> response = keyValueOperations.get("myapp", Secrets.class);
        Assertions.assertNotNull(response);
        assertEquals(secrets.password, response.getData().getPassword());


        HashicorpVaultProvider hashicorp = HashicorpVaultProvider.getInstance();
        VaultProvider.register(hashicorp);

        VaultProvider h = VaultProvider.forName(HashicorpVaultProvider.NAME);
        assertEquals(hashicorp, h);

        assertDoesNotThrow(() -> {
            AbstractVaultProvider.DecodeSpec spec = AbstractVaultProvider.parseSpec(String.format("vault:hashicorp:%s/dev-secrets/myapp", endpointAddress));
            assertNotNull(spec);
            assertEquals(HashicorpVaultProvider.NAME, spec.name());
            assertEquals(String.format("%s/dev-secrets/myapp", endpointAddress), spec.locator());
        });

        assertDoesNotThrow(() -> {
            AbstractVaultProvider.parseSpec("vault:hashicorp:endpoint1:/dev-secrets/myapp");
        });

        assertThrows(InvalidVaultSpecException.class, () -> {
            hashicorp.client(new AbstractVaultProvider.DecodeSpec(hashicorp.getName(), "endpoint1:/dev-secrets/myapp"));
        });

        hashicorp.addNamedEndpoint("endpointa", endpointAddress);

        assertThrows(InvalidVaultSpecException.class, () -> {
            hashicorp.client(new AbstractVaultProvider.DecodeSpec(hashicorp.getName(), "endpoint1:/dev-secrets/myapp"));
        });

        hashicorp.addNamedEndpoint("endpoint1", endpointAddress);

        assertThrows(IllegalArgumentException.class, () -> {
            hashicorp.client(new AbstractVaultProvider.DecodeSpec(hashicorp.getName(), "endpoint1:/dev-secrets/myapp"));
        });

        hashicorp.addAuthForName("endpoint1", token);
        File propertiesFile = hashicorp.savePropertiesFile();

        assertTrue(propertiesFile.exists());

        ObjectMapper mapper = new ObjectMapper();
        assertDoesNotThrow(() -> {
            AbstractVaultProvider.DecodeSpec spec = new AbstractVaultProvider.DecodeSpec(hashicorp.getName(), "endpoint1:/dev-secrets/data/myapp");
            VaultClient client = hashicorp.client(spec);
            client.authenticate();
            String result = client.read(spec);
            Secrets secrets1 = mapper.readValue(result, Secrets.class);
            assertEquals(secrets.password, secrets1.password);
            assertEquals(secrets.username, secrets1.username);
        });

        keyValueOperations.delete("myapp");
    }

    public static class Secrets {

        String username;
        String password;

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }
    }
}
