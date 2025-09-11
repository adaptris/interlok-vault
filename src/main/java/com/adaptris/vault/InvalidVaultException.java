package com.adaptris.vault;

public class InvalidVaultException extends VaultException {
    String vaultName;

    public InvalidVaultException(String vaultName) {
        this(null, vaultName);
    }

    public InvalidVaultException(String message, String vaultName) {
        super(message);
        this.vaultName = vaultName;
    }
}
