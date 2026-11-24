package net.usfi.dao;

import org.springframework.stereotype.Component;

/**
 * Implémentation "base de données" de IDao.
 * @Component("d") : utilisé uniquement par la version Spring annotations.
 * (Spring propose aussi @Repository, spécialisé pour la couche DAO.)
 */
@Component("d")
public class DaoImpl implements IDao {
    @Override
    public double getData() {
        System.out.println("Version base de données");
        double temperature = 23;
        return temperature;
    }
}
