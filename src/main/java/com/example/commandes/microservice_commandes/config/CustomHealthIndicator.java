package com.example.commandes.microservice_commandes.config;

import com.example.commandes.microservice_commandes.services.CommandeService;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class CustomHealthIndicator implements HealthIndicator {

    private final CommandeService commandeService;

    public CustomHealthIndicator(CommandeService commandeService) {
        this.commandeService = commandeService;
    }

    @Override
    public Health health() {
        long count = commandeService.count();
        if (count > 0) {
            return Health.up()
                    .withDetail("message", "Il y a " + count + " commandes en base.")
                    .build();
        } else {
            return Health.down()
                    .withDetail("message", "Aucune commande en base.")
                    .build();
        }
    }
}
