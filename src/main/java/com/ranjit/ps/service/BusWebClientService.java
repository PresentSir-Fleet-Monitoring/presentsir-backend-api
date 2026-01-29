package com.ranjit.ps.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ranjit.ps.model.Bus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Service
public class BusWebClientService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    private static final String GITHUB_URL =
            "https://raw.githubusercontent.com/ranjit485/elite-backend-configration/refs/heads/main/routes.json";

    public BusWebClientService(WebClient.Builder builder, ObjectMapper objectMapper) {
        this.webClient = builder.build();
        this.objectMapper = objectMapper;
    }

    public List<Bus> getAllBuses() {
        return webClient.get()
                .uri(GITHUB_URL)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(this::mapToBusList)
                .onErrorReturn(List.of())
                .block();
    }

    private List<Bus> mapToBusList(JsonNode rootNode) {

        if (rootNode == null || !rootNode.isArray()) {
            return List.of();
        }

        List<Bus> buses = new ArrayList<>();

        for (JsonNode node : rootNode) {
            Bus bus = new Bus();
            bus.setBusId(node.get("busId").asLong());
            bus.setRouteName(node.get("routeName").asText());
            buses.add(bus);
        }

        return buses;
    }
}
