package com.github.rmohr.cockpit.debugger;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;
import java.util.regex.Pattern;

import javax.websocket.DeploymentException;
import javax.websocket.WebSocketContainer;

import org.apache.http.auth.AuthenticationException;
import org.glassfish.tyrus.client.ClientManager;
import org.glassfish.tyrus.client.ClientProperties;
import org.glassfish.tyrus.client.SslEngineConfigurator;

import com.github.rmohr.cockpit.client.Client;
import com.github.rmohr.cockpit.client.SslUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CockpitDebugger {
    public static void main(String[] args)
            throws IOException, DeploymentException, AuthenticationException, URISyntaxException,
            NoSuchAlgorithmException, KeyManagementException {

        final String url = System.getProperty("url", "wss://localhost:9090/cockpit");
        final String user = System.getProperty("user", "root");
        final String password = System.getProperty("password", "foobar");
        final String checkCertificates = System.getProperty("checkCertificates", "false");
        Client clientEndpoint;
        if (checkCertificates.equals(true)) {
            clientEndpoint = new Client(url, user, password, new ConsoleMessageHandler());
        } else {
            clientEndpoint = new Client(createInsecureSocketContainer(),
                    SslUtils.createInsecureHttpClient(user, password),
                    url,
                    new ConsoleMessageHandler());
        }
        clientEndpoint.connect();

        final Scanner scanner = new Scanner(System.in);
        scanner.useDelimiter(Pattern.compile("[\\r\\n;]+"));
        String message = "";
        while (true) {
            String line = scanner.nextLine();
            if (line.equals("---")) {
                clientEndpoint.sendMessage(message);
                message = "";
            } else {
                message = message + line + "\n";
            }
        }
    }

    private static WebSocketContainer createInsecureSocketContainer() throws NoSuchAlgorithmException,
            KeyManagementException {
        ClientManager client = ClientManager.createClient();
        SslEngineConfigurator sslEngineConfigurator = new SslEngineConfigurator(SslUtils.createInsecureSslContext());
        sslEngineConfigurator.setHostnameVerifier(SslUtils.createInsecureHostNameVerifier());
        client.getProperties().put(ClientProperties.SSL_ENGINE_CONFIGURATOR, sslEngineConfigurator);
        client.getProperties().put(ClientProperties.LOG_HTTP_UPGRADE, true);
        return client;
    }
}
