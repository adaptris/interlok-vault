package com.adaptris.vault;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.vault.authentication.ClientAuthentication;
import org.springframework.vault.client.VaultEndpoint;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.support.VaultToken;

public class SpringHashicorpClient implements VaultClient {
    private boolean isAuthenticated = false;
    private VaultEndpoint vaultEndpoint;
    private ClientAuthentication clientAuthentication;
    private VaultTemplate vaultTemplate;
    private VaultToken vaultToken;

    protected SpringHashicorpClient(VaultEndpoint endpoint, ClientAuthentication clientAuthentication) {
        this.vaultEndpoint = endpoint;
        this.clientAuthentication = clientAuthentication;
        this.vaultTemplate = new VaultTemplate(endpoint, clientAuthentication);
    }

    @Override
    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    @Override
    public void authenticate() {
        vaultToken = clientAuthentication.login();
        isAuthenticated = true;
    }

    @Override
    public String read(AbstractVaultProvider.DecodeSpec spec) {
        return vaultTemplate.read(spec.path(), JsonNode.class).getData().get("data").toString();
    }
}
