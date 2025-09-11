package com.adaptris.vault;

import java.util.HashMap;
import java.util.Map;

public interface VaultProvider {

    Map<String, VaultProvider> PROVIDERS = new HashMap<>();

    String getName();

    String getEndpointForName(String name);

    /**
     * Gets a usable VaultClient
     * @return VaultClient
     */
    VaultClient client(AbstractVaultProvider.DecodeSpec decodeSpec) throws InvalidVaultSpecException;

    VaultClient namedClient(String name, String path) throws InvalidVaultSpecException;


    static void register(VaultProvider provider) {
        PROVIDERS.put(provider.getName(), provider);
    }

    static void deregister(AbstractVaultProvider provider) {
        PROVIDERS.remove(provider.getName());
    }

    static VaultProvider forName(String name) {
        return PROVIDERS.get(name);
    }

}
