package com.adaptris.vault;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.*;

public abstract class AbstractVaultProvider implements VaultProvider {

    public static final String PROPERTY_CONF_FILE = "vault.conf.file";
    public static final String PROPERTY_TOKEN = "token";
    public static final String PROPERTY_ENDPOINT = "endpoint";
    public static final String DEFAULT_NAME = "";
    public static final String DEFAULT_PROPERTIES_FILE = "vault.properties";

    private Properties vaultProperties = new Properties();

    protected String buildConfProperty(String key) {
        return String.format("%s.%s", getName(), key);
    }

    protected void addNamedEndpoint(String name, String endpoint) {
        vaultProperties.put(buildConfProperty(String.format("%s.%s", PROPERTY_ENDPOINT, name)), endpoint);
    }

    @Override
    public String getEndpointForName(String name) {
        return (String)vaultProperties.get(buildConfProperty(String.format("%s.%s", PROPERTY_ENDPOINT, name)));
    }

    protected void addAuthForName(String name, String auth) {
        vaultProperties.put(buildConfProperty(String.format("%s.%s", PROPERTY_TOKEN, name)), auth);
    }

    public String getAuthForName(String name) {
        return (String)vaultProperties.get(buildConfProperty(String.format("%s.%s", PROPERTY_TOKEN, name)));
    }

    protected void clear() {
        vaultProperties.clear();
    }

    protected void configure(Properties properties) {
        vaultProperties.putAll(properties);
    }

    protected Properties getVaultProperties() {
        return vaultProperties;
    }

    // spec should be in the format <provider>:<path>, but if passed is vault:<provider>:<path>
    // it should be accepted as well
    public static String decode(DecodeSpec spec) throws InvalidVaultSpecException {
        VaultProvider provider = VaultProvider.forName(spec.name());
        assert provider != null;
        VaultClient client = provider.client(spec);
        if (!client.isAuthenticated()) {
            client.authenticate();
        }
        return client.read(spec);
    }

    public static String decode(String spec) throws InvalidVaultSpecException, InvalidVaultException {
        DecodeSpec decodeSpec = parseSpec(spec);
        return decode(decodeSpec);
    }

    public static DecodeSpec parseSpec(String spec) throws InvalidVaultException, InvalidVaultSpecException {
        return DecodeSpec.parse(spec);
    }

    public static class DecodeSpec {
        final static String SEPARATOR = ":";
        public final static String VAULT_NAME = "vault";
        private final String name;
        private final String locator;
        private URL locatorURL = null;
        private String namedEndpoint = null;
        private String namedEndpointPath = null;

        public DecodeSpec(String name, String locator) throws InvalidVaultSpecException {
            this.name = name;
            this.locator = locator;
            try {
                locatorURL = URI.create(locator).toURL();
            } catch (MalformedURLException | IllegalArgumentException ex) {
                String[] tokens = locator.split(SEPARATOR);
                if (tokens.length > 1) {
                    namedEndpoint = tokens[0];
                    namedEndpointPath = tokens[1];
                } else {
                    throw new InvalidVaultSpecException(locator);
                }
            }
        }

        public String getNamedEndpoint() {
            return namedEndpoint;
        }

        public static DecodeSpec parse(String spec) throws InvalidVaultSpecException, InvalidVaultException {
            String[] tokens = spec.split(SEPARATOR);
            if (tokens.length < 2) {
                throw new InvalidVaultSpecException("Invalid vault spec: " + spec, spec);
            }
            if (tokens[0].equals(VAULT_NAME) && tokens.length < 3) {
                throw new InvalidVaultSpecException("Invalid vault spec: " + spec, spec);
            } else {
                tokens = Arrays.copyOfRange(tokens, 1, tokens.length);
            }
            String name = tokens[0];
            if (!PROVIDERS.containsKey(name)) {
                throw new InvalidVaultException(name);
            }
            String locator = String.join(SEPARATOR, List.of(Arrays.copyOfRange(tokens, 1, tokens.length)));
            return new DecodeSpec(name, locator);
        }

        public String toString() {
            return name + SEPARATOR + locator;
        }

        public boolean isNamedEndpoint() {
            return locatorURL == null && namedEndpoint != null;
        }

        public String name() {
            return name;
        }

        public String locator() {
            return locator;
        }

        public String path() {
            if (isNamedEndpoint()) {
                return namedEndpointPath;
            } else {
                return locatorURL.getPath();
            }
        }
    }
}
