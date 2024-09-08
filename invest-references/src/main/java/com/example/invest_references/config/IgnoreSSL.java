package com.example.invest_references.config;

import org.springframework.context.annotation.Configuration;

import javax.net.ssl.*;
import java.security.cert.X509Certificate;

@Configuration
public class IgnoreSSL {

    public void disableSslVerification() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }
                        public void checkClientTrusted(X509Certificate[] certs, String authType){}
                        public void checkServerTrusted(X509Certificate[] certs, String authType){}
                    }
            };

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            HostnameVerifier allHostsValid = new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session){
                    return true;
                }
            };

            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
