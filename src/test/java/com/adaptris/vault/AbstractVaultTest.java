package com.adaptris.vault;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.containers.ComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.containers.wait.strategy.WaitStrategy;

import java.io.File;
import java.net.InetSocketAddress;
import java.time.Duration;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AbstractVaultTest {
    public static final String VAULT_SERVICE_NAME = "vault-1";
    public static final int VAULT_PORT = 8200;
    public static final String VAULT_ROOT_TOKEN = "SKkFwiwsA0Nm8Xpp70ykhVMH";
    protected static WaitStrategy defaultWaitStrategy = Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(30));

    private ComposeContainer environment;

    @BeforeAll
    public void setup() throws Exception {
        this.environment = setupContainers();
        this.environment.start();
    }

    @AfterAll
    public void teardown() throws Exception {
        if (this.environment != null) {
            this.environment.stop();
        }
    }


    protected ComposeContainer setupContainers() throws Exception {
        return new ComposeContainer(new File("docker-compose.yaml"))
                .withExposedService(VAULT_SERVICE_NAME, VAULT_PORT, defaultWaitStrategy);
    }

    protected InetSocketAddress getHostAddressForService(String serviceName, int port) {
        return new InetSocketAddress(environment.getServiceHost(serviceName, port), environment.getServicePort(serviceName, port));
    }

}
