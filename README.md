# Compte rendu — Inversion de contrôle et injection des dépendances

**Étudiant :** CHAKRANI Ilyass — École d'Ingénierie de Mundiapolis (cycle ingénieur 2A)
**Sujet :** Application « fermée à la modification et ouverte à l'extension » : couplage faible + injection des dépendances (statique, dynamique, Spring XML, Spring annotations)
**Source :** vidéos de cours sur l'injection des dépendances (exemple DAO / Métier / Présentation)

---

## 1. Objectif

Montrer comment créer une application Java **fermée à la modification** et **ouverte à l'extension**, ce qui facilite la maintenance. Cela repose sur deux principes :

1. **Le couplage faible** : dépendre des *interfaces* et non des *classes* (« programmer des interfaces, pas des implémentations »).
2. **L'injection des dépendances** : un objet reçoit les objets dont il a besoin au lieu de les créer lui-même (par `new`).

Dans une application basée sur l'**inversion de contrôle** (IoC), le développeur se concentre sur le code **métier** ; le framework (Spring) s'occupe du code **technique** : création des objets, injection, transactions, sécurité, journalisation…

## 2. Architecture du projet

Application en trois couches, dans le package de base `net.usfi` (bonne pratique : tous les packages descendent d'un même package de base, ce qui simplifie le scan de Spring).

```
inversion-injection-dependances/
├── pom.xml                      # Maven, Java 21, Spring 6.2.3 (core, context, beans)
├── config.txt                   # config de l'instanciation dynamique (racine du projet)
├── README.md                    # ce compte rendu
└── src/main/
    ├── java/net/usfi/
    │   ├── dao/            IDao, DaoImpl              (version base de données)
    │   ├── ext/            DaoImplV2                  (extension : version capteur)
    │   ├── metier/         IMetier, MetierImpl
    │   └── presentation/   Pres1, PresNullPointer, Pres2, Pres2Setter,
    │                       PresSpringXML, PresSpringAnnotation
    └── resources/          config.xml                 (beans Spring, version XML)
```

## 3. Partie 1 — Réalisation

### Étape 1 — Interface `IDao` avec la méthode `getData`

Le **concepteur** crée l'interface : il définit ce qui doit être implémenté.

```java
public interface IDao {
    double getData();
}
```

### Étape 2 — Implémentation de `IDao`

Le **développeur** implémente le besoin. Ici, la « version base de données » : pas de vraie base, on affiche un message et on retourne une température.

```java
public class DaoImpl implements IDao {
    @Override
    public double getData() {
        System.out.println("Version base de données");
        double temperature = 23;
        return temperature;
    }
}
```

Une seconde implémentation (`DaoImplV2`, « version capteur », température 12) est ajoutée dans le package `ext` pour simuler une évolution demandée plus tard par le client.

> On dit « je crée **une** implémentation de l'interface » (et non « *l'*implémentation ») : une interface sert justement à permettre plusieurs implémentations dans le futur.

### Étape 3 — Interface `IMetier` avec la méthode `calcul`

```java
public interface IMetier {
    double calcul();
}
```

### Étape 4 — Implémentation de `IMetier` avec couplage faible

`MetierImpl` dépend de l'**interface** `IDao` et non d'une classe. L'attribut n'est **pas initialisé** (valeur `null` par défaut) : écrire `= new DaoImpl()` créerait un couplage fort.

```java
public class MetierImpl implements IMetier {
    private IDao dao;                       // couplage faible, non initialisé

    public MetierImpl() {}                  // pour l'injection via le setter
    public MetierImpl(IDao dao) {           // pour l'injection via le constructeur
        this.dao = dao;
    }

    @Override
    public double calcul() {
        double t = dao.getData();
        return t * 20 * Math.PI / 2 * Math.cos(t);   // règle métier fictive
    }

    public void setDao(IDao dao) { this.dao = dao; }
}
```

Tant que `dao` n'est pas injecté, l'appel de `calcul()` provoque une **`NullPointerException`** (« dépendance non satisfaite », voir `PresNullPointer`) :

```
Exception in thread "main" java.lang.NullPointerException:
Cannot invoke "net.usfi.dao.IDao.getData()" because "this.dao" is null
    at net.usfi.metier.MetierImpl.calcul(MetierImpl.java:34)
```

Deux moyens d'injecter : le **setter** (constructeur sans paramètres puis `setDao`) ou le **constructeur avec paramètres** (en une seule opération, à l'instanciation). Le constructeur est préféré dans la pratique.

### Étape 5 — Injection des dépendances

#### a) Par instanciation statique (`Pres1`)

```java
DaoImpl d = new DaoImpl();
MetierImpl metier = new MetierImpl();
metier.setDao(d);                                   // injection via le setter
System.out.println("Résultat = " + metier.calcul());

MetierImpl metier2 = new MetierImpl(d);             // injection via le constructeur
```

Sortie obtenue :

```
Version base de données
[setter]       Résultat = -385.0071895219764
Version base de données
[constructeur] Résultat = -385.0071895219764
Version capteur
[capteur]      Résultat = 318.1254476947994
```

**Analyse :**
- Couche DAO : fermée à la modification (elle ne dépend d'aucune classe) et ouverte à l'extension (interface).
- Couche métier : fermée à la modification (elle ne dépend que d'interfaces) et ouverte à l'extension.
- Couche présentation : **encore ouverte à la modification**. Pour passer de `DaoImpl` à `DaoImplV2`, il faut changer le code source (`new DaoImplV2()`) et recompiler. Cette classe est une *fabrique* (factory) : la maintenance est au moins concentrée sur une seule classe.

#### b) Par instanciation dynamique (`Pres2`, `Pres2Setter`)

Fichier `config.txt` (à la **racine** du projet, pas dans `src`) :

```
net.usfi.dao.DaoImpl
net.usfi.metier.MetierImpl
```

```java
Scanner scanner = new Scanner(new File("config.txt"));

String daoClassName = scanner.nextLine().trim();
Class<?> cDao = Class.forName(daoClassName);                       // charge la classe en mémoire
IDao dao = (IDao) cDao.getConstructor().newInstance();             // crée l'objet

String metierClassName = scanner.nextLine().trim();
Class<?> cMetier = Class.forName(metierClassName);
IMetier metier = (IMetier) cMetier.getConstructor(IDao.class).newInstance(dao); // injection via constructeur
```

Variante par le setter (`Pres2Setter`), via la réflexion :

```java
IMetier metier = (IMetier) cMetier.getConstructor().newInstance();
Method setDao = cMetier.getMethod("setDao", IDao.class);
setDao.invoke(metier, dao);          // équivalent dynamique de metier.setDao(dao)
```

Sortie obtenue avec `config.txt` ci-dessus :

```
Version base de données
Résultat = -385.0071895219764
```

Pour basculer vers la version capteur, on modifie **uniquement** `config.txt` (`net.usfi.ext.DaoImplV2` en ligne 1), sans toucher au code :

```
Version capteur
Résultat = 318.1254476947994
```

La couche présentation devient donc **fermée à la modification et ouverte à l'extension**. Exceptions classiques rencontrées avec la réflexion : `ClassNotFoundException` (nom de classe/package mal écrit), `InstantiationException`, `IllegalAccessException` (constructeur privé), `NoSuchMethodException`, `InvocationTargetException`.

**Limite :** il faut trois lignes pour créer un objet, et le code se répète (copier-coller-remplacer). Quand on en arrive là, c'est qu'un framework existe déjà : **Spring**.

#### c) Avec le framework Spring

Dépendances Maven (`pom.xml`) : `spring-core`, `spring-context`, `spring-beans` (version 6.2.3).

**Version XML** (`PresSpringXML`, fichier `src/main/resources/config.xml`) :

```xml
<bean id="d" class="net.usfi.dao.DaoImpl"/>
<bean id="metier" class="net.usfi.metier.MetierImpl">
    <property name="dao" ref="d"/>          <!-- appelle setDao(d) : injection via le setter -->
</bean>
```

```java
ApplicationContext context = new ClassPathXmlApplicationContext("config.xml");
IMetier metier = (IMetier) context.getBean("metier");   // par id (cast)
IMetier metier2 = context.getBean(IMetier.class);       // par interface (sans cast)
System.out.println("Résultat = " + metier.calcul());
```

Au démarrage, Spring lit le XML, crée les objets (beans), les stocke dans son contexte (une map nom → objet) et fait l'injection. Pour injecter via le constructeur, on remplace `<property>` par `<constructor-arg ref="d"/>`. Pour passer à la version capteur, on remplace la classe du bean `d` par `net.usfi.ext.DaoImplV2`.

**Version annotations** (`PresSpringAnnotation`) :

```java
@Component("d")
public class DaoImpl implements IDao { ... }

@Component("metier")
public class MetierImpl implements IMetier {
    @Autowired
    public MetierImpl(@Qualifier("d") IDao dao) { this.dao = dao; }   // injection via constructeur
    ...
}
```

```java
ApplicationContext context =
    new AnnotationConfigApplicationContext("net.usfi.dao", "net.usfi.metier");
IMetier metier = context.getBean(IMetier.class);
System.out.println("Résultat = " + metier.calcul());
```

Points importants :
- `@Component` demande à Spring d'instancier la classe et de l'enregistrer comme bean ; `@Autowired` demande l'injection automatique (sur l'attribut ou sur le constructeur).
- Spring **scanne** les packages indiqués. Avec un package de base (`"net.usfi"`), il scanne tous les sous-packages.
- S'il existe **plusieurs beans** du même type (ex. `d` et `d2`), Spring lève `NoUniqueBeanDefinitionException` / `UnsatisfiedDependencyException` : il faut préciser le bean avec `@Qualifier("d")` ou `@Qualifier("d2")`. La valeur du qualifier pourra plus tard venir d'un fichier de configuration (`application.properties` avec Spring Boot).
- Spring propose des annotations spécialisées : `@Repository` (couche DAO), `@Service` (couche métier), `@Controller` (couche web).

## 4. Tableau récapitulatif

| Technique | Qui crée les objets ? | Changer de version du DAO | Couche présentation fermée à la modification ? |
|---|---|---|---|
| Instanciation statique (`new`) | Le développeur | Modifier le code source | Non |
| Instanciation dynamique (réflexion) | Le développeur (code générique) | Modifier `config.txt` | Oui |
| Spring XML | Spring | Modifier `config.xml` | Oui |
| Spring annotations | Spring | `@Qualifier` / packages scannés | Oui |

## 5. Conclusion

Pour créer une application fermée à la modification et ouverte à l'extension, il faut (1) utiliser le **couplage faible** (dépendre des interfaces) et (2) faire l'**injection des dépendances**. Avec Spring, ce n'est plus le développeur mais le framework qui crée et contrôle les objets, d'où le nom d'**inversion de contrôle** : le développeur écrit le code métier, Spring gère le code technique. Dans la pratique, on préfère les **annotations** au XML, et l'injection par le **constructeur** à celle par le setter.

## 6. Exécution

Depuis IntelliJ : clic droit sur la classe de `presentation` → *Run*. Le dossier de travail doit être la racine du projet (nécessaire pour `config.txt`).

En ligne de commande (JDK 21 + Maven) :

```bash
mvn compile
mvn exec:java -Dexec.mainClass=net.usfi.presentation.Pres1
mvn exec:java -Dexec.mainClass=net.usfi.presentation.Pres2
mvn exec:java -Dexec.mainClass=net.usfi.presentation.PresSpringXML
mvn exec:java -Dexec.mainClass=net.usfi.presentation.PresSpringAnnotation
```

## 7. Workflow Git demandé

```bash
git init
git add .
git commit -m "Initial commit : structure du projet"
git branch -M main
git remote add origin <URL_DU_REPOSITORY>
git push -u origin main
# puis un commit + push toutes les ~30 min, et un dernier commit en fin de séance
```
