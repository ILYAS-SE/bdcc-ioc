package net.usfi.presentation;

import net.usfi.metier.IMetier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/** Version 3 : Spring, configuration XML (src/main/resources/config.xml). */
public class PresSpringXML {
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("config.xml");

        // Par identifiant (cast nécessaire)
        IMetier metier = (IMetier) context.getBean("metier");
        System.out.println("Résultat = " + metier.calcul());

        // Par type/interface (pas de cast ; exception s'il y a plusieurs beans du même type)
        IMetier metier2 = context.getBean(IMetier.class);
        System.out.println("Résultat = " + metier2.calcul());
    }
}
