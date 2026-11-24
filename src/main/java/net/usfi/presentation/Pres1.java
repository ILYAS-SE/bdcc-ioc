package net.usfi.presentation;

import net.usfi.dao.DaoImpl;
import net.usfi.ext.DaoImplV2;
import net.usfi.metier.MetierImpl;

/**
 * Version 1 : injection des dépendances par INSTANCIATION STATIQUE (new).
 * Pour changer de version du DAO, il faut modifier le code source -> couche non fermée à la modification.
 */
public class Pres1 {
    public static void main(String[] args) {
        // a) Injection via le setter
        DaoImpl d = new DaoImpl();                 // 1. objet DAO
        MetierImpl metier = new MetierImpl();      // 2. objet métier (dao = null pour l'instant)
        metier.setDao(d);                          // 3. injection via le setter
        System.out.println("[setter]       Résultat = " + metier.calcul());

        // b) Injection via le constructeur (préférée)
        MetierImpl metier2 = new MetierImpl(d);
        System.out.println("[constructeur] Résultat = " + metier2.calcul());

        // c) Basculer vers la version capteur = modifier le code (new DaoImplV2())
        MetierImpl metier3 = new MetierImpl(new DaoImplV2());
        System.out.println("[capteur]      Résultat = " + metier3.calcul());
    }
}
