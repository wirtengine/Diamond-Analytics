package com.diamondanalytics.backend.service.external;

import com.diamondanalytics.backend.dto.odds.OddsApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class OddsApiClient {

    @Autowired
    private WebClient oddsWebClient;

    @Value("${odds.api.key}")
    private String apiKey;

    public List<OddsApiResponse> getMlbOdds() {
        return oddsWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/sports/baseball_mlb/odds/")
                        .queryParam("apiKey", apiKey)
                        .queryParam("regions", "us")
                        .queryParam("markets", "h2h,spreads,totals")
                        .queryParam("oddsFormat", "decimal")
                        .build())
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class).flatMap(error -> {
                            return Mono.error(new RuntimeException("Error en Odds API: " + error));
                        }))
                .bodyToFlux(OddsApiResponse.class)
                .collectList()
                .block(); // bloquea para simplificar; en producción usar async con CompletableFuture
    }
}