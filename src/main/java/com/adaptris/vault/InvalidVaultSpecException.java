package com.adaptris.vault;

public class InvalidVaultSpecException extends VaultException {
    String spec;

    public InvalidVaultSpecException(String spec) {
        this(null, spec);
    }

    public InvalidVaultSpecException(String message, String spec) {
        super(message);
        this.spec = spec;
    }
}
