package com.github.rmohr.cockpit.client;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.websocket.ClientEndpointConfig;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Client extends javax.websocket.Endpoint {

    private static final String COCKPIT_PROTOCOL = "cockpit1";
    private static final String LOGIN_PATH = "/login";
    private static final String SOCKET_PATH = "/socket";
    private final CloseableHttpClient httpClient;
    private final WebSocketContainer client;
    private final String endpointURI;
    private Session userSession;
    private MessageHandler messageHandler;

    public Client(WebSocketContainer client,
            CloseableHttpClient httpClient,
            String endpointURI,
            MessageHandler messageHandler) {
        requireNonNull(httpClient);
        requireNonNull(messageHandler);
        requireNonNull(endpointURI);
        requireNonNull(client);
        this.httpClient = httpClient;
        this.messageHandler = messageHandler;
        this.client = client;
        this.endpointURI = endpointURI;
    }

    public Client(
            String endpointURI,
            String username,
            String password,
            MessageHandler messageHandler) {
        requireNonNull(endpointURI);
        requireNonNull(username);
        requireNonNull(password);
        this.messageHandler = messageHandler;
        this.client = ContainerProvider.getWebSocketContainer();
        this.endpointURI = endpointURI;
        this.httpClient = defaultHttpClient(username, password);
    }

    public void connect() throws URISyntaxException, NoSuchAlgorithmException, IOException, AuthenticationException,
            DeploymentException {
        final URI loginPath = getLoginPath(endpointURI);
        final URI socketPath = getSocketPath(endpointURI);
        client.connectToServer(this, ClientEndpointConfig.Builder.create()
                        .configurator(new SessionConfigurator(login(loginPath),
                                endpointURI.startsWith(
                                        "wss")))
                        .preferredSubprotocols(Arrays.asList(COCKPIT_PROTOCOL))
                        .build(),
                socketPath);
    }

    @Override
    public void onOpen(Session userSession, EndpointConfig endpointConfig) {
        this.userSession = userSession;
        this.userSession.addMessageHandler(messageHandler);
        log.debug("Connection established");
    }

    @Override
    public void onClose(Session userSession, CloseReason reason) {
        this.userSession = null;
        log.debug("Connection closed");
    }

    public void sendMessage(String message) {
        log.debug("Sending {}", message);
        this.userSession.getAsyncRemote().sendText(message);
    }

    private URI getSocketPath(String baseUri) throws URISyntaxException {
        if (baseUri.endsWith("/")) {
            baseUri = baseUri.substring(0, baseUri.length() - 2);
        }
        return new URI(baseUri + SOCKET_PATH);
    }

    private URI getLoginPath(String baseUri) throws URISyntaxException {
        if (baseUri.endsWith("/")) {
            baseUri = baseUri.substring(0, baseUri.length() - 2);
        }
        if (baseUri.startsWith("wss")) {
            baseUri = "https" + baseUri.substring(3);
        } else {
            baseUri = "http" + baseUri.substring(2);
        }
        return new URI(baseUri + LOGIN_PATH);
    }

    private static CloseableHttpClient defaultHttpClient(String username, String password) {
        CredentialsProvider credProvider = new BasicCredentialsProvider();
        credProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(username, password));
        return HttpClients.custom().setDefaultCredentialsProvider(credProvider).build();
    }

    private String login(URI endpointURI) throws IOException,
            AuthenticationException, NoSuchAlgorithmException {
        HttpHost host = new HttpHost(endpointURI.getHost(), endpointURI.getPort(), endpointURI.getScheme());
        HttpGet httpGet = new HttpGet(endpointURI.getRawPath());

        AuthCache authCache = new BasicAuthCache();
        BasicScheme basicScheme = new BasicScheme();
        authCache.put(host, basicScheme);
        HttpClientContext clientContext = HttpClientContext.create();
        clientContext.setAuthCache(authCache);
        CloseableHttpResponse response = httpClient.execute(host, httpGet, clientContext);
        if (response.getStatusLine().getStatusCode() != 200) {
            throw new AuthenticationException();
        }
        return response.getFirstHeader("Set-Cookie").getElements()[0].getName() + "=" + response.getFirstHeader
                ("Set-Cookie").getElements()[0].getValue();
    }
}
