package com.adaptris.vault;

public interface VaultClient {
    boolean isAuthenticated();
    void authenticate();
    String read(AbstractVaultProvider.DecodeSpec spec);
}
