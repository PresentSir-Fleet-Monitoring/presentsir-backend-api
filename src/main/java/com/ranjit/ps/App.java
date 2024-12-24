package com.ranjit.ps;

import com.ranjit.ps.model.Bus;
import com.ranjit.ps.service.BusQService;
import com.ranjit.ps.service.BusService;
import com.ranjit.ps.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication(scanBasePackages = "com.ranjit.ps")
public class App implements CommandLineRunner {

    @Autowired
    private BusQService busQService;
    @Autowired
    private BusService busService;

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Override
    public void run(String... args) {

        List<Bus> buses = busService.getAllBuses();

        for (int i = 0; i < buses.size(); i++) {
            busQService.createBusQueue(buses.get(i).getBusId());
        }

        System.out.println("All Bus Queues Created Successfully");
    }

}
