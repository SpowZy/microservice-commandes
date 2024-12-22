package com.example.commandes.microservice_commandes.repositories;

import com.example.commandes.microservice_commandes.entities.Commande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommandeRepository extends JpaRepository<Commande, Long> {

}
