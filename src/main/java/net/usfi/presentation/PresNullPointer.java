package net.usfi.presentation;

import net.usfi.metier.MetierImpl;

/**
 * Démonstration de l'erreur classique : dépendance non satisfaite.
 * dao n'a pas été injecté (null) -> NullPointerException dans calcul().
 */
public class PresNullPointer {
    public static void main(String[] args) {
        MetierImpl metier = new MetierImpl();
        System.out.println("Résultat = " + metier.calcul()); // NullPointerException
    }
}
