package com.adaptris.security.password;

import com.adaptris.security.exc.PasswordException;
import com.adaptris.vault.InvalidVaultException;
import com.adaptris.vault.InvalidVaultSpecException;
import com.adaptris.vault.AbstractVaultProvider;

public class VaultPasswordImpl extends PasswordImpl {

    public VaultPasswordImpl() {
    }

    @Override
    public String encode(String plainText, String charset) throws PasswordException {
        throw new PasswordException("Password encoding not supported");
    }

    @Override
    public String decode(String encryptedPassword, String charset) throws PasswordException {
        try {
            return AbstractVaultProvider.decode(encryptedPassword);
        } catch (InvalidVaultSpecException | InvalidVaultException e) {
            throw new PasswordException(e);
        }
    }

    @Override
    public boolean canHandle(String type) {
        return type != null && type.toLowerCase().startsWith(AbstractVaultProvider.DecodeSpec.VAULT_NAME);
    }
}
