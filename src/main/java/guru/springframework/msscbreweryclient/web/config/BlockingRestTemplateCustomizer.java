package guru.springframework.msscbreweryclient.web.config;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Created by jt on 2019-08-08.
 */
@Component // don't forget
// JT likes Apache HTTP Client> mature + easy logging setup
public class BlockingRestTemplateCustomizer implements RestTemplateCustomizer {
// implements RestTemplateCustomizer>injects a RestTemplate + allows customization 

// apache logging setup: ..\resources\application.properties>add>logging.level.org.apache.http=debug
// in console>">> arrows out" shows:- URL hit, headers, User Agent, encoding 
// "<< arrows in" shows:- http status etc ... low level logging, good for debugging, careful! bad for performance 

// defensive programming>when spring builds this these final properties can't change
    private final Integer maxTotalConnections;
    private final Integer defaultMaxTotalConnetions;
    private final Integer connectionRequestTimeout;
    private final Integer socketTimeout;

// ${>spring expression language>get values from property store
    public BlockingRestTemplateCustomizer(@Value("${sfg.maxtotalconnections}") Integer maxTotalConnections,
                                          @Value("${sfg.defaultmaxtotalconnections}") Integer defaultMaxTotalConnetions,
                                          @Value("${sfg.connectionrequesttimeout}")Integer connectionRequestTimeout,
                                          @Value("${sfg.sockettimeout}")Integer socketTimeout) {
        this.maxTotalConnections = maxTotalConnections;
        this.defaultMaxTotalConnetions = defaultMaxTotalConnetions;
        this.connectionRequestTimeout = connectionRequestTimeout;
        this.socketTimeout = socketTimeout;
    }
    
    // Implementation>setup:- 1. connectionManager 2. RequestConfig 3. Client
    // Client injected into RequestFactory>RequestFactory set on RestTemplate
    public ClientHttpRequestFactory clientHttpRequestFactory(){
        // start setting up apache specific stuff 
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(); // from apache 
        connectionManager.setMaxTotal(maxTotalConnections);
        connectionManager.setDefaultMaxPerRoute(defaultMaxTotalConnetions);

        RequestConfig requestConfig = RequestConfig
                .custom()
                .setConnectionRequestTimeout(connectionRequestTimeout)
                .setSocketTimeout(socketTimeout)
                .build(); 

        // standard setup according to documentation, can configure more
        CloseableHttpClient httpClient = HttpClients
                .custom()
                .setConnectionManager(connectionManager)
                .setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy())
                .setDefaultRequestConfig(requestConfig)
                .build();

        // returns Spring's ClientHttpRequestFactory implementation 
        return new HttpComponentsClientHttpRequestFactory(httpClient); 
    }

    // Spring injects in RestTemplate, here we set up the apache flavour
    @Override
    public void customize(RestTemplate restTemplate) {
        restTemplate.setRequestFactory(this.clientHttpRequestFactory());
    }
}
