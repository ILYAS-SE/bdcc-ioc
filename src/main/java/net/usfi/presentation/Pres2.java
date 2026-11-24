package net.usfi.presentation;

import net.usfi.dao.IDao;
import net.usfi.metier.IMetier;

import java.io.File;
import java.util.Scanner;

/**
 * Version 2 : INSTANCIATION DYNAMIQUE (réflexion) avec injection via le CONSTRUCTEUR.
 * Les noms des classes sont lus dans config.txt (à la racine du projet).
 * Pour changer de version : on modifie config.txt, pas le code source.
 */
public class Pres2 {
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(new File("config.txt"));

        // Couche DAO : ligne 1 du fichier
        String daoClassName = scanner.nextLine().trim();
        Class<?> cDao = Class.forName(daoClassName);                 // charge la classe en mémoire
        IDao dao = (IDao) cDao.getConstructor().newInstance();       // constructeur sans paramètres

        // Couche métier : ligne 2 du fichier
        String metierClassName = scanner.nextLine().trim();
        Class<?> cMetier = Class.forName(metierClassName);
        IMetier metier = (IMetier) cMetier.getConstructor(IDao.class).newInstance(dao); // injection via constructeur

        System.out.println("Résultat = " + metier.calcul());
        scanner.close();
    }
}
