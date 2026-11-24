package net.usfi.ext;

import net.usfi.dao.IDao;
import org.springframework.stereotype.Component;

/**
 * Implémentation "capteur" de IDao : une extension ajoutée plus tard,
 * sans modifier ni la couche DAO ni la couche métier.
 */
@Component("d2")
public class DaoImplV2 implements IDao {
    @Override
    public double getData() {
        System.out.println("Version capteur");
        double temperature = 12;
        return temperature;
    }
}
