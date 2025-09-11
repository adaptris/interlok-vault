package com.adaptris.vault;

import com.adaptris.security.password.Password;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class InterlokTest {
    @Mock
    private VaultProvider hashicorpVaultProvider;
    @Mock
    private VaultClient vaultClient;

    /**
     * This will test the service loading of the <code>com.adaptris.security.password.VaultPasswordImpl</code>
     * @throws Exception
     */
    @Test
    public void testPasswordLoader() throws Exception {
        final String secret = "secret";
        when(hashicorpVaultProvider.getName()).thenReturn(HashicorpVaultProvider.NAME);
        when(hashicorpVaultProvider.client(any())).thenReturn(vaultClient);
        when(vaultClient.read(any())).thenReturn(secret);

        VaultProvider.register(hashicorpVaultProvider);
        String endpoint = "endpoint1";
        String path = "path1";
        String decoded = Password.decode(String.format("%s:%s:%s:%s", AbstractVaultProvider.DecodeSpec.VAULT_NAME, HashicorpVaultProvider.NAME, endpoint, path));
        Assertions.assertNotNull(decoded);
        Assertions.assertEquals(secret, decoded);
    }
}
