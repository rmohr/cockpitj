package com.github.rmohr.cockpit.debugger;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Scanner;
import java.util.regex.Pattern;

import javax.websocket.DeploymentException;

import org.apache.http.auth.AuthenticationException;

import com.github.rmohr.cockpit.client.Client;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CockpitDebugger {
    public static void main(String[] args)
            throws IOException, DeploymentException, AuthenticationException, URISyntaxException {

        final String url = System.getProperty("url", "ws://localhost:9090/cockpit");
        final String user = System.getProperty("user", "root");
        final String password = System.getProperty("password", "foobar");
        final Client clientEndpoint = new Client(url, user, password, new ConsoleMessageHandler());

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
}
