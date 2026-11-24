package net.usfi.presentation;

import net.usfi.metier.IMetier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Version 4 : Spring, annotations (@Component, @Autowired, @Qualifier).
 * Spring scanne les packages indiqués et instancie les classes annotées.
 * Seuls dao et metier sont scannés ici : DaoImplV2 (package ext) est ignoré.
 * Pour utiliser la version capteur : scanner aussi "net.usfi.ext" (ou le package de base "net.usfi")
 * et mettre @Qualifier("d2") dans MetierImpl.
 */
public class PresSpringAnnotation {
    public static void main(String[] args) {
        ApplicationContext context =
                new AnnotationConfigApplicationContext("net.usfi.dao", "net.usfi.metier");
        // Équivalent en scannant tout le package de base : new AnnotationConfigApplicationContext("net.usfi");

        IMetier metier = context.getBean(IMetier.class);
        System.out.println("Résultat = " + metier.calcul());
    }
}
