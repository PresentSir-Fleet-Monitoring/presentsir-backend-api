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

    private final WebClient webClient;
    private final BusRepository busRepository;

    public BusWebClientService(WebClient.Builder webClientBuilder, BusRepository busRepository) {
        this.webClient = webClientBuilder
                .baseUrl("https://api.jsonbin.io/v3/b/68fdd11ed0ea881f40bcb67c")
                .build();
        this.busRepository = busRepository;
    }

    public List<Bus> getAllBuses() {
        try {
            Mono<List<Bus>> busesMono = webClient.get()
                    .uri("?meta=false")
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<Bus>>() {});

            return busesMono.block();
        } catch (Exception e) {
            System.err.println("Error fetching from API, using DB fallback: " + e.getMessage());
            return busRepository.findAll();
        }
    }
}
// Add Another fallback url