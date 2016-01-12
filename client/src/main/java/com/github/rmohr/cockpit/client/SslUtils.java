package com.github.rmohr.cockpit.client;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class SslUtils {

    public static HostnameVerifier createInsecureHostNameVerifier() {
        return new AnyHostNameVerifier();
    }

    static class AnyHostNameVerifier implements HostnameVerifier {

        @Override public boolean verify(String s, SSLSession sslSession) {
            return true;
        }
    }

    public static TrustManager[] createInsecureTrustManager() {
        return new TrustManager[] { new X509TrustManager() {
            @Override
            public void checkClientTrusted(java.security.cert.X509Certificate[] x509Certificates, String s)
                    throws CertificateException {
            }

            @Override
            public void checkServerTrusted(java.security.cert.X509Certificate[] x509Certificates, String s)
                    throws CertificateException {
            }

            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        }
        };
    }

    public static SSLContext createInsecureSslContext() throws KeyManagementException, NoSuchAlgorithmException {
        // Install the insecure trust manager and the insecure host name verifier
        TrustManager[] trustAllCerts = SslUtils.createInsecureTrustManager();
        SSLContext sc = SSLContext.getInstance("TLS");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        return sc;
    }

    public static CloseableHttpClient createInsecureHttpClient(String username, String password)
            throws NoSuchAlgorithmException, KeyManagementException {
        CredentialsProvider credProvider = new BasicCredentialsProvider();
        credProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(username, password));
        return HttpClients.custom()
                .setDefaultCredentialsProvider(credProvider)
                .setSSLHostnameVerifier(SslUtils.createInsecureHostNameVerifier())
                .setSSLContext(SslUtils.createInsecureSslContext())
                .build();
    }

}

