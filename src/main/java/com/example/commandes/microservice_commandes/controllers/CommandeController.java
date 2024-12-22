package com.example.commandes.microservice_commandes.controllers;

import com.example.commandes.microservice_commandes.entities.Commande;
import com.example.commandes.microservice_commandes.services.CommandeService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/commandes")
@RefreshScope // Permet la prise en compte dynamique de la propriété 'mes-config-ms.commandes-last'
public class CommandeController {

    private final CommandeService commandeService;

    // On injecte la propriété personnalisée depuis le Config Server
    @Value("${mes-config-ms.commandes-last:10}")
    private int commandesLastDays;

    public CommandeController(CommandeService commandeService) {
        this.commandeService = commandeService;
    }

    /**
     * Récupérer toutes les commandes en base
     */
    @GetMapping
    public List<Commande> getAllCommandes() {
        return commandeService.findAll();
    }

    /**
     * Récupérer les commandes reçues 'commandesLastDays' derniers jours
     */
    @GetMapping("/recent")
    public List<Commande> getRecentCommandes() {
        LocalDate dateLimite = LocalDate.now().minusDays(commandesLastDays);
        return commandeService.findAll()
                .stream()
                .filter(cmd -> cmd.getDate() != null && cmd.getDate().isAfter(dateLimite))
                .collect(Collectors.toList());
    }

    /**
     * Récupérer une commande par son id
     */
    @GetMapping("/{id}")
    public Commande getCommandeById(@PathVariable Long id) {
        return commandeService.findById(id)
                .orElseThrow(() -> new RuntimeException("Commande introuvable avec l'id : " + id));
    }

    /**
     * Créer une nouvelle commande
     */
    @PostMapping
    public Commande createCommande(@RequestBody Commande commande) {
        // Par exemple on force la date à aujourd'hui si elle n'est pas renseignée
        if (commande.getDate() == null) {
            commande.setDate(LocalDate.now());
        }
        return commandeService.saveCommande(commande);
    }

    /**
     * Mettre à jour une commande
     */
    @PutMapping("/{id}")
    public Commande updateCommande(@PathVariable Long id, @RequestBody Commande commande) {
        Commande existing = commandeService.findById(id)
                .orElseThrow(() -> new RuntimeException("Commande introuvable avec l'id : " + id));

        existing.setDescription(commande.getDescription());
        existing.setQuantite(commande.getQuantite());
        existing.setDate(commande.getDate());
        existing.setMontant(commande.getMontant());

        return commandeService.saveCommande(existing);
    }

    /**
     * Supprimer une commande
     */
    @DeleteMapping("/{id}")
    public void deleteCommande(@PathVariable Long id) {
        commandeService.deleteById(id);
    }

    /**
     * Pour vérifier rapidement la valeur de 'commandesLastDays'
     */
    @GetMapping("/configValue")
    public String getConfigValue() {
        return "mes-config-ms.commandes-last = " + commandesLastDays;
    }
}
