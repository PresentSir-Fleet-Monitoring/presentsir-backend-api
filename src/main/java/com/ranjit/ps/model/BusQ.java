package com.ranjit.ps.model;

import java.util.LinkedList;
import java.util.Queue;
import java.util.List;
import java.util.ArrayList;

/**
 * BusQ class manages a queue of location clients (e.g., subscribers) for a specific bus.
 * It provides methods to add, retrieve, remove, and process clients in the queue
 * while maintaining a fixed maximum size for the queue.
 *
 * Copyright © 2024 Ranjit
 * Author: Ranjit
 */
public class BusQ {
    private final long busId; // Unique identifier for the bus
    private final int MAX_QUEUE_SIZE = 6; // Maximum size of the queue
    private final Queue<String> locationClientsQueue; // Queue to store client information (emails)

    /**
     * Constructor to initialize the BusQ object with a specific bus ID.
     *
     * @param busId The unique identifier for the bus.
     */
    public BusQ(long busId) {
        this.busId = busId;
        this.locationClientsQueue = new LinkedList<>();
    }

    /**
     * Retrieves the unique bus ID.
     *
     * @return The bus ID.
     */
    public long getBusId() {
        return busId;
    }

    /**
     * Adds a new client (email) to the queue.
     * If the queue size exceeds the maximum allowed size,
     * the oldest client is removed to make room for the new one.
     *
     * @param clientSessionId The email of the client to be added to the queue.
     */
    public void addClient(String clientSessionId) {
        if (locationClientsQueue.size() >= MAX_QUEUE_SIZE) {
            // Remove the oldest client from the queue
            String removedClient = locationClientsQueue.poll();
            System.out.println("Queue is full. Removed oldest client: " + removedClient);
        }
        locationClientsQueue.add(clientSessionId);
        System.out.println("Client : " + clientSessionId + " added to BusQ ID: " + busId);
    }

    /**
     * Retrieves the next client in the queue without removing it.
     *
     * @return The email of the next client, or null if the queue is empty.
     */
    public String peekNextClient() {
        return locationClientsQueue.peek();
    }

    /**
     * Retrieves a list of all clients currently in the queue.
     * This method does not modify the queue.
     *
     * @return A list containing the emails of all clients in the queue.
     */
    public List<String> getAllClients() {
        return new ArrayList<>(locationClientsQueue); // Create a copy of the queue as a list
    }

    /**
     * Removes a specific client from the queue by their email.
     * The method maintains the order of the remaining clients in the queue.
     *
     * @param clientSessionId The email of the client to be removed.
     * @return True if the client was successfully removed, false if the client was not found.
     */
    public boolean removeClientBySessionId(String clientSessionId) {
        if (locationClientsQueue.contains(clientSessionId)) {
            Queue<String> tempQueue = new LinkedList<>();
            // Rebuild the queue without the specified client
            while (!locationClientsQueue.isEmpty()) {
                String currentClient = locationClientsQueue.poll();
                if (!currentClient.equals(clientSessionId)) {
                    tempQueue.add(currentClient);
                } else {
                    System.out.println("Client: " + clientSessionId + " removed from BusQ ID: " + busId);
                }
            }
            locationClientsQueue.addAll(tempQueue); // Restore the queue
            return true; // Client was removed successfully
        } else {
            System.out.println("Client: " + clientSessionId + " not found in BusQ ID: " + busId);
            return false; // Client not found in the queue
        }
    }

    /**
     * Processes and removes each client in the queue.
     * This method simulates processing by iterating through all clients
     * and printing each one as it is removed.
     */
    public void processAndClearQueue() {
        System.out.println("Processing all clients in the queue for bus " + busId);
        while (!locationClientsQueue.isEmpty()) {
            String client = locationClientsQueue.poll();
            System.out.println("Processed client: " + client);
        }
    }
}
