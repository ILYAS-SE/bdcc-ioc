package net.usfi.metier;

import net.usfi.dao.IDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Couplage faible : on dépend de l'interface IDao, pas d'une classe.
 * La variable n'est pas initialisée (valeur par défaut : null) :
 * c'est l'injection des dépendances qui lui affectera un objet.
 * (Spring propose aussi @Service, spécialisé pour la couche métier.)
 */
@Component("metier")
public class MetierImpl implements IMetier {

    // Alternative : injection directe sur l'attribut avec @Autowired (+ @Qualifier si plusieurs beans)
    private IDao dao;

    // Constructeur sans paramètres : nécessaire pour l'injection via le setter
    // (instanciation statique/dynamique avec setDao, et version Spring XML <property>)
    public MetierImpl() {
    }

    // Constructeur avec paramètres : injection via le constructeur (la meilleure pratique).
    // @Autowired + @Qualifier("d") : version Spring annotations, on choisit le bean "d".
    @Autowired
    public MetierImpl(@Qualifier("d") IDao dao) {
        this.dao = dao;
    }

    @Override
    public double calcul() {
        double t = dao.getData();
        // Règle métier fictive, juste pour avoir un calcul
        double res = t * 20 * Math.PI / 2 * Math.cos(t);
        return res;
    }

    /** Injecter dans l'attribut dao un objet d'une classe qui implémente IDao. */
    public void setDao(IDao dao) {
        this.dao = dao;
    }
}
