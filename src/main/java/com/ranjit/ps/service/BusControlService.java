package com.ranjit.ps.service;

import com.ranjit.ps.model.BusController;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class BusControlService {

    private final Map<Long, BusController> activeBuses = new ConcurrentHashMap<>();

    public synchronized boolean registerDriver(long busId, String sessionId, String email) {

        if (activeBuses.containsKey(busId)) {
            return false; // Already controlled
        }

        activeBuses.put(busId, new BusController(sessionId, email));
        return true;
    }

    public void removeDriverIfOwner(long busId, String sessionId) {
        BusController controller = activeBuses.get(busId);
        if (controller != null && controller.getSessionId().equals(sessionId)) {
            activeBuses.remove(busId);
        }
    }

    public BusController getController(long busId) {
        return activeBuses.get(busId);
    }

    public boolean isAuthorized(long busId, String sessionId) {
        BusController controller = activeBuses.get(busId);
        return controller != null && controller.getSessionId().equals(sessionId);
    }
    public Map<Long, BusController> getControllerMap() {
        return activeBuses;
    }

}
