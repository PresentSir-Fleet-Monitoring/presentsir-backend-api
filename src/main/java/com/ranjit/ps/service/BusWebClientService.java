package com.ranjit.ps.service;

import com.ranjit.ps.model.Bus;
import com.ranjit.ps.repository.BusRepository;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class BusWebClientService {

    private final WebClient primaryClient;
    private final WebClient fallbackClient;

    public BusWebClientService(WebClient.Builder builder) {

        this.primaryClient = builder
                .baseUrl("https://api.jsonbin.io/v3/b/68fdd11ed0ea881f40bcb67c")
                .build();

        this.fallbackClient = builder
                .baseUrl("https://raw.githubusercontent.com/ranjit485/elite-backend-configration/refs/heads/main/routes.json")
                .build();
    }

    public List<Bus> getAllBuses() {
        return fetch(primaryClient)
                .onErrorResume(ex -> {
                    System.err.println("Primary failed, switching to fallback");
                    return fetch(fallbackClient);
                })
                .onErrorReturn(List.of())
                .block();
    }

    private Mono<List<Bus>> fetch(WebClient client) {

        String baseUrl = client.mutate().build().toString();

        boolean needsMetaFalse = baseUrl.contains("jsonbin.io");

        return client.get()
                .uri(uriBuilder -> needsMetaFalse
                        ? uriBuilder.queryParam("meta", "false").build()
                        : uriBuilder.build()
                )
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Bus>>() {});
    }
}
