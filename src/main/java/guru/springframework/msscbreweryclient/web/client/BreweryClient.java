package guru.springframework.msscbreweryclient.web.client;

import guru.springframework.msscbreweryclient.web.model.BeerDto;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

/**
 * Created by jt on 2019-04-23.
 */
// sfg.brewery>application.properties, ignoreUnknownFields=false>immediate failure if property missing
@ConfigurationProperties(prefix = "sfg.brewery", ignoreUnknownFields = false)
@Component  // don't forget
public class BreweryClient {

    public final String BEER_PATH_V1 = "/api/v1/beer/"; // careful with slashes
    private String apihost; // should be able to set as final in future releases

    private final RestTemplate restTemplate;

    // inject in RestTemplateBuilder (recommended)>allows configuration for edge cases e.g. security / HTTP client library>
    // is preconfigured>can override>picks up global values, local RestTemplate won't pick up things configured by spring boot.
    public BreweryClient(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public BeerDto getBeerById(UUID uuid){
        return restTemplate.getForObject(apihost + BEER_PATH_V1 + uuid.toString(), BeerDto.class);
    }

    // ..\mssc-brewery-client\src\main\resources\application.properties>sfg.brewery.apihost=http://localhost:8080
    public void setApihost(String apihost) {
        this.apihost = apihost;
    } 
}
