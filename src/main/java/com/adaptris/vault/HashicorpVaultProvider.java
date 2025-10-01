package com.adaptris.vault;

import org.springframework.vault.authentication.ClientAuthentication;
import org.springframework.vault.authentication.TokenAuthentication;
import org.springframework.vault.client.VaultEndpoint;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class HashicorpVaultProvider extends AbstractVaultProvider {
    public static final String NAME = "hashicorp";
    private Map<String, ClientAuthentication> defaultClientAuthentication;
    private Map<String, VaultClient> endpointClients = new HashMap<>();
    private static final HashicorpVaultProvider INSTANCE = new HashicorpVaultProvider();

    private HashicorpVaultProvider() {
    }

    public static HashicorpVaultProvider getInstance() {
        return INSTANCE;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public VaultClient client(DecodeSpec decodeSpec) throws InvalidVaultSpecException {
        if (decodeSpec.isNamedEndpoint()) {
            return namedClient(decodeSpec.getNamedEndpoint(), decodeSpec.path());
        } else {
            return client(decodeSpec.locator());
        }
    }

    protected VaultClient client(String endpoint) throws InvalidVaultSpecException {
        if (!endpointClients.containsKey(endpoint)) {
            VaultClient newClient = newClient(endpoint);
            endpointClients.put(endpoint, newClient);
            return newClient;
        } else return endpointClients.get(endpoint);
    }

    @Override
    public VaultClient namedClient(String name, String path) throws InvalidVaultSpecException {
        String endpoint = getEndpointForName(name);
        if (endpoint != null) {
            String endpointPath = String.format("%s%s", endpoint, path);
            if (!endpointClients.containsKey(endpointPath)) {
                VaultClient newClient = newNamedClient(name, endpointPath);
                endpointClients.put(endpointPath, newClient);
            }
            return client(endpointPath);
        } else throw new InvalidVaultSpecException("Invalid named endpoint", name);
    }

    protected VaultClient newClient(String endpointStr) throws InvalidVaultSpecException {
        try {
            VaultEndpoint endpoint = buildVaultEndpoint(endpointStr);
            return new SpringHashicorpClient(endpoint, new TokenAuthentication(getAuthForName(DEFAULT_NAME)));
        } catch (MalformedURLException e) {
            // if bad url, check to see if its a named client
            throw new InvalidVaultSpecException("Invalid endpoint", endpointStr);
        }
    }

    private VaultEndpoint buildVaultEndpoint(String endpointStr) throws MalformedURLException {
        URL url = new URL(endpointStr);
        VaultEndpoint endpoint = new VaultEndpoint();
        endpoint.setScheme(url.getProtocol());
        endpoint.setHost(url.getHost());
        endpoint.setPort(url.getPort());
        String path = url.getPath();
        if (path != null && !path.isBlank() && !path.startsWith("/")) {
            endpoint.setPath(path);
        }
        return endpoint;
    }

    private ClientAuthentication buildClientAuthentication(String auth) {
        return new TokenAuthentication(auth);
    }

    protected VaultClient newNamedClient(String name, String endpointStr) throws InvalidVaultSpecException {
        try {
            VaultEndpoint endpoint = buildVaultEndpoint(endpointStr);
            String auth = getAuthForName(name);
            if (auth == null) {
                auth = getAuthForName(DEFAULT_NAME);
            }
            ClientAuthentication clientAuthentication = buildClientAuthentication(auth);
            return new SpringHashicorpClient(endpoint, clientAuthentication);
        } catch (MalformedURLException e) {
            // if bad url, check to see if its a named client
            throw new InvalidVaultSpecException("Invalid endpoint", endpointStr);
        }
    }

    protected File savePropertiesFile() {
        return savePropertiesFile(true);
    }

    protected File savePropertiesFile(boolean refresh) {
        File file = new File(DEFAULT_PROPERTIES_FILE);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            getInstance().getVaultProperties().store(fos, "");
            if (refresh) {
                getInstance().clear();
                configure();
            }
            return file;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected static boolean deletePropertiesFile(boolean clear) {
        if (clear) {
            getInstance().clear();
        }
        File file = new File(DEFAULT_PROPERTIES_FILE);
        return file.delete();
    }

    protected static boolean deletePropertiesFile() {
        return deletePropertiesFile(false);
    }


    protected static void configure() {
        String confPropertyFile = System.getProperty(AbstractVaultProvider.PROPERTY_CONF_FILE, AbstractVaultProvider.DEFAULT_PROPERTIES_FILE);
        File authFile = new File(confPropertyFile);
        if (authFile.isFile() && authFile.canRead()) {
            Properties properties = new Properties();
            try (FileInputStream fis = new FileInputStream(authFile)) {
                properties.load(fis);
            } catch (IOException e2) {
                throw new RuntimeException(e2);
            }
            getInstance().configure(properties);
        }
    }
}
