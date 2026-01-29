package com.ranjit.ps.service;

import com.ranjit.ps.model.Bus;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class BusWebClientService {

    private final WebClient webClient;

    public BusWebClientService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("https://api.jsonbin.io/v3/b/68fdd11ed0ea881f40bcb67c")
                .build();
    }

    public List<Bus> getAllBuses() {
        Mono<List<Bus>> busesMono = webClient.get()
                .uri("?meta=false")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Bus>>() {});

        return busesMono.block(); // blocking call
    }
}
