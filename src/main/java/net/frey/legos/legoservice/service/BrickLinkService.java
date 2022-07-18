package net.frey.legos.legoservice.service;

import lombok.RequiredArgsConstructor;
import net.frey.legos.legoservice.exception.OAuthException;
import net.frey.legos.legoservice.oauth.BLAuthSigner;
import net.frey.legos.legoservice.ro.BrickLinkSet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BrickLinkService {
    @Value("${store.consumerKey}")
    private String consumerKey;

    @Value("${store.consumerSecret}")
    private String consumerSecret;

    @Value("${store.tokenValue}")
    private String tokenValue;

    @Value("${store.tokenSecret}")
    private String tokenSecret;

    private WebClient client;

    @PostConstruct
    void buildWebClient() {
        client = WebClient.builder()
            .baseUrl("https://api.bricklink.com/api/store/v1")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .defaultHeader(HttpHeaders.AUTHORIZATION, "OAuth")
            .build();
    }

    public Mono<BrickLinkSet> fetchSet(String setNumber) {
        Map<String, String> params;

        try {
            params = BLAuthSigner.getFinalOAuthParams(consumerKey, consumerSecret, tokenValue, tokenSecret);
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException | InvalidKeyException e) {
            return Mono.error(new OAuthException("Could not generate OAuth parameters.", e));
        }

        return client.get()
            .uri(String.format("/items/set/%s", setNumber))
            .headers(httpHeaders -> params.forEach(httpHeaders::set))
            .retrieve()
            .bodyToMono(BrickLinkSet.class);
    }
}
