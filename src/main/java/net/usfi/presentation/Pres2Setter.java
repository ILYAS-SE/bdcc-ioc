package net.usfi.presentation;

import net.usfi.dao.IDao;
import net.usfi.metier.IMetier;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Scanner;

/**
 * Instanciation dynamique avec injection via le SETTER (programmation par réflexion).
 * Plus lourd que le constructeur : il faut retrouver et invoquer la méthode setDao.
 */
public class Pres2Setter {
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(new File("config.txt"));

        Class<?> cDao = Class.forName(scanner.nextLine().trim());
        IDao dao = (IDao) cDao.getConstructor().newInstance();

        Class<?> cMetier = Class.forName(scanner.nextLine().trim());
        IMetier metier = (IMetier) cMetier.getConstructor().newInstance(); // constructeur sans paramètres

        // Équivalent dynamique de : metier.setDao(dao);
        Method setDao = cMetier.getMethod("setDao", IDao.class);
        setDao.invoke(metier, dao);

        System.out.println("Résultat = " + metier.calcul());
        scanner.close();
    }
}
