package eu.cryptoeuro.bankgateway.config;

import javax.net.ssl.SSLContext;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;

import eu.cryptoeuro.bankgateway.services.lhv.LhvConnectApi;
import eu.cryptoeuro.bankgateway.services.lhv.LhvConnectApiImpl;

import java.net.URL;

/**
 * Spring configuration: service layer
 *
 * @author Erko Hansar
 */
@Configuration
public class ServiceConfig {

    @Value("${lhv.connect.keyStore.URL}")
    private String lhvConnectKeyStoreURL;
    @Value("${lhv.connect.keyStore.password}")
    private String lhvConnectKeyStorePassword;

    @Bean
    public LhvConnectApi lhvConnect() throws Exception {
        CloseableHttpClient lhvHttpClient = null;

        if (StringUtils.isNotBlank(lhvConnectKeyStoreURL) && StringUtils.isNotBlank(lhvConnectKeyStorePassword)) {
            lhvHttpClient = HttpClients.custom()
                    .addInterceptorFirst(new HttpComponentsMessageSender.RemoveSoapHeadersInterceptor())
                    .setSSLSocketFactory(new SSLConnectionSocketFactory(createSslContext()))
                    .build();
        }

        return new LhvConnectApiImpl(lhvHttpClient);
    }

    ///// PRIVATE METHODS /////

    private SSLContext createSslContext() {
        SSLContext sslContext;
        try {
            char[] password = lhvConnectKeyStorePassword.toCharArray();
            sslContext = SSLContexts.custom()
                    .loadKeyMaterial(new URL(lhvConnectKeyStoreURL), password, password)
                    .build();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to set up SSL WebService template!", e);
        }

        return sslContext;
    }

}
