package com.adaptris.vault;

public class VaultException extends Exception {
    public VaultException(String message) {
        super(message);
    }
    public VaultException(String message, Throwable cause) {
        super(message, cause);
    }

    public VaultException() {

    }
}
