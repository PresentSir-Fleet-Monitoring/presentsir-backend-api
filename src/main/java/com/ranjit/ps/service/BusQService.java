package com.ranjit.ps.service;

import com.ranjit.ps.model.BusQ;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
/**
 * BusQService is a Spring service class that manages a collection of BusQ objects.
 * It allows creating, retrieving, updating, and processing location clients associated with buses.
 *
 * Copyright © 2024 Ranjit
 * Author: Ranjit
 */
@Service
public class BusQService {
    // Map to store BusQ objects with the bus ID as the key
    private final Map<Long, BusQ> buses = new HashMap<>();

    /**
     * Creates a new BusQ object for a specific bus ID and stores it in the buses map.
     *
     * @param busId The unique ID of the bus.
     */
    public void createBusQueue(long busId) {
        BusQ busQ = new BusQ(busId);
        buses.put(busId, busQ);
        System.out.println("Created bus with ID: " + busId);
    }

    /**
     * Retrieves the BusQ object for a specific bus ID.
     *
     * @param busId The unique ID of the bus.
     * @return The BusQ object, or null if no BusQ is found for the given ID.
     */
    public BusQ getBusQueue(long busId) {
        return buses.get(busId);
    }

    /**
     * Adds a client (email) to the queue of a specific bus.
     *
     * @param busId The ID of the bus.
     * @param clientSessionId The email of the client to add.
     */
    public void addClientToBusQueue(long busId, String clientSessionId) {
        BusQ busQ = buses.get(busId);
        if (busQ != null) {
            busQ.addClient(clientSessionId);
        } else {
            System.out.println("Bus with ID " + busId + " not found.");
        }
    }

    /**
     * Retrieves the next client (email) in the queue for a specific bus without removing it.
     *
     * @param busId The ID of the bus.
     * @return The email of the next client, or null if the queue is empty or the bus ID is invalid.
     */
    public String getNextClientInBusQueue(long busId) {
        BusQ busQ = buses.get(busId);
        if (busQ != null) {
            String nextClient = busQ.peekNextClient();
            System.out.println("Next client in bus " + busId + ": " + nextClient);
            return nextClient;
        } else {
            System.out.println("Bus with ID " + busId + " not found.");
            return null;
        }
    }

    /**
     * Removes a specific client (clientSessionId) from the queue of a specific bus.
     *
     * @param busId The ID of the bus.
     * @param clientSessionId The email of the client to remove.
     * @return A message indicating whether the client was successfully removed.
     */
    public String removeClientFromBusQueue(long busId, String clientSessionId) {
        BusQ busQ = buses.get(busId);
        if (busQ != null) {
            boolean removed = busQ.removeClientBySessionId(clientSessionId);
            if (removed) {
                return "Client " + clientSessionId + " removed from BusQ with ID: " + busId;
            } else {
                return "Client " + clientSessionId + " not found in BusQ with ID: " + busId;
            }
        } else {
            return "Bus with ID " + busId + " not found.";
        }
    }

    /**
     * Processes and clears all client queues for all buses.
     * This method iterates through all buses and processes their queues.
     */
    public void processAllBusQueues() {
        for (BusQ busQ : buses.values()) {
            busQ.processAndClearQueue();
        }
    }

    //TODO add more filters
    // this method returns clientSessionId of client from busQ
    public String getLocationProviderClient(long busId){
        BusQ busQ = buses.get(busId);
        System.out.println(busQ.peekNextClient());
        // This method returns peek from q by id
        return busQ.peekNextClient();
    }

}
