# FitTrack — Application de fitness tracking

**FitTrack** est une application web de suivi d'entraînement qui transforme un simple gestionnaire
d'activités en un véritable outil de fitness tracking : séances détaillées, bibliothèque d'exercices,
estimation des calories, mesures corporelles, objectifs hebdomadaires et tableau de bord analytique.

Application **Java 17 / Spring Boot 3.2**, rendue côté serveur avec **Thymeleaf**, sécurisée par
**Spring Security**, persistée avec **Spring Data JPA** sur **H2** (par défaut) ou **MySQL** (profil dédié).

---

## Sommaire

- [Aperçu](#aperçu)
- [Fonctionnalités](#fonctionnalités)
- [Captures d'écran / documentation](#documentation)
- [Stack technique](#stack-technique)
- [Démarrage rapide](#démarrage-rapide)
- [Compte de démonstration](#compte-de-démonstration)
- [Configuration](#configuration)
- [Architecture du projet](#architecture-du-projet)
- [Modèle de données](#modèle-de-données)
- [Calculs métier](#calculs-métier)
- [API REST](#api-rest)
- [Tests](#tests)
- [Sécurité](#sécurité)
- [Déploiement](#déploiement)
- [Licence](#licence)

---

## Aperçu

| Espace | Contenu |
|---|---|
| **Tableau de bord** | KPI de la semaine, progression de l'objectif, série de semaines actives, graphiques 7 jours / 8 semaines, courbe de poids, records personnels, dernières séances |
| **Séances** | Création, édition, suppression, filtres par type et période, détail avec exercices, séries, charges, volume soulevé |
| **Exercices** | Bibliothèque de 32 exercices (MET, groupe musculaire), recherche, exercices personnels |
| **Mesures** | Poids, masse grasse, tour de taille / poitrine, IMC, objectif de poids, courbe d'évolution |
| **Profil** | Identité, taille, âge, niveau d'activité, objectif hebdomadaire, métabolisme de base et TDEE estimés |

Le compte de démonstration arrive avec **10 semaines d'historique réaliste** (40 séances, 10 mesures).

## Fonctionnalités

### Authentification et comptes
- Inscription avec validation (e-mail unique, mot de passe ≥ 8 caractères, confirmation).
- Connexion / déconnexion via Spring Security, mots de passe hachés en **BCrypt**.
- Rôles `ROLE_USER` / `ROLE_ADMIN`, pages publiques limitées à l'accueil, la connexion et l'inscription.
- Isolation stricte : chaque utilisateur n'accède qu'à ses propres séances, mesures et exercices personnels.

### Suivi d'entraînement
- 5 types de séance : musculation, cardio, HIIT, mobilité, sport collectif.
- Durée, intensité (faible / modérée / élevée), date, notes libres.
- Exercices détaillés : séries, répétitions, charge, durée, distance, commentaire.
- Calcul automatique des **calories** (MET × intensité × poids × durée) et du **volume soulevé** (séries × reps × charge).
- Filtres et synthèse (nombre de séances, minutes, calories) sur la liste.

### Mesures et objectifs
- Historique du poids, masse grasse et mensurations avec courbe SVG côté serveur.
- IMC et catégorie calculés automatiquement, écart avec l'objectif de poids.
- Objectif hebdomadaire de séances et **série de semaines consécutives** actives.
- Records personnels : charge maximale et séance la plus longue.

### Expérience utilisateur
- Interface sombre responsive, sans dépendance CSS/JS externe (fonctionne hors ligne).
- Graphiques générés en CSS/SVG par Thymeleaf, aucune librairie front.
- Messages de succès, validations de formulaire, états vides explicites, page d'erreur personnalisée.

## Documentation

Les livrables de documentation se trouvent dans le dossier [`docs/`](docs) :

- `docs/FitTrack-Documentation.pdf` — documentation technique complète (contexte, architecture, modèle de données, sécurité, tests, guide utilisateur, roadmap).
- `docs/FitTrack-Presentation.pptx` — présentation de soutenance (16:9, 15 slides).

## Stack technique

| Couche | Technologie |
|---|---|
| Langage | Java 17 |
| Framework | Spring Boot 3.2.5 |
| Web / vues | Spring MVC, Thymeleaf 3, fragments, `thymeleaf-extras-springsecurity6` |
| Sécurité | Spring Security 6 (form login, BCrypt, CSRF) |
| Persistance | Spring Data JPA / Hibernate |
| Base de données | H2 (fichier local, profil par défaut), MySQL 8 (profil `mysql`) |
| Validation | Jakarta Bean Validation (Hibernate Validator) |
| Build | Maven 3.9 |
| Tests | JUnit 5, AssertJ, Spring Boot Test, MockMvc, Spring Security Test |

## Démarrage rapide

### Pré-requis
- **JDK 17** ou supérieur
- **Maven 3.8+** (ou le wrapper `mvnw` fourni)

### Lancement (H2, aucune installation de base de données)

```bash
cd ActivityManagementSystem-fitTrack   # dossier du projet
./mvnw spring-boot:run
# ou : mvn spring-boot:run
```

L'application démarre sur **http://localhost:8080**.

La base est créée automatiquement dans `./data/fittrack.mv.db` et peuplée au premier démarrage
(bibliothèque d'exercices, compte démo, historique). Pour repartir de zéro : arrêter l'application
puis supprimer le dossier `data/`.

Console H2 (développement) : `http://localhost:8080/h2-console`
JDBC URL : `jdbc:h2:file:./data/fittrack` — utilisateur `sa`, mot de passe vide.

### Lancement avec MySQL

```sql
CREATE DATABASE fittrack CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=mysql
# ou avec des identifiants personnalisés
DB_USER=root DB_PASSWORD=secret SPRING_PROFILES_ACTIVE=mysql mvn spring-boot:run
```

### Construction du JAR exécutable

```bash
mvn clean package
java -jar target/fittrack-1.0.0.jar
```

## Compte de démonstration

| Champ | Valeur |
|---|---|
| E-mail | `demo@fittrack.app` |
| Mot de passe | `Demo1234!` |

Vous pouvez aussi créer un compte vierge depuis `/register` pour tester le parcours complet.

## Configuration

Fichier principal : `src/main/resources/application.properties`.

| Propriété | Valeur par défaut | Description |
|---|---|---|
| `server.port` | `8080` | Port HTTP (surchargeable par `PORT`) |
| `spring.datasource.url` | `jdbc:h2:file:./data/fittrack` | Base H2 locale |
| `spring.jpa.hibernate.ddl-auto` | `update` | Schéma mis à jour automatiquement |
| `spring.web.locale` | `fr` | Locale d'affichage (dates en français) |
| `app.seed.enabled` | `true` | Jeu de données de démonstration |
| `spring.h2.console.enabled` | `true` | Console H2 (à désactiver en production) |

Profil MySQL : `application-mysql.properties` (`DB_USER`, `DB_PASSWORD`, base `fittrack`).

## Architecture du projet

```
fittrack/
├── pom.xml
├── README.md
├── docs/
│   ├── FitTrack-Documentation.pdf
│   └── FitTrack-Presentation.pptx
└── src/
    ├── main/
    │   ├── java/com/fittrack/
    │   │   ├── FitTrackApplication.java
    │   │   ├── config/           # SecurityConfig, DataSeeder
    │   │   ├── domain/           # User, Role, Workout, WorkoutExercise, Exercise, Measurement + enums
    │   │   ├── repository/       # Spring Data JPA repositories
    │   │   ├── service/          # UserService, WorkoutService, DashboardService, FitnessCalculator...
    │   │   └── web/              # Controllers MVC + API + DTO + vue DashboardView
    │   └── resources/
    │       ├── templates/        # Thymeleaf (fragments/layout, dashboard, workouts...)
    │       ├── static/css/app.css
    │       ├── application.properties
    │       └── application-mysql.properties
    └── test/
        ├── java/com/fittrack/    # Tests d'intégration et unitaires
        └── resources/application-test.properties
```

Couches : **web → service → repository → domain**, avec DTO de formulaire validés et
`GlobalModelAdvice` pour exposer l'utilisateur courant aux vues.

## Modèle de données

```
users 1 ──── n workouts 1 ──── n workout_exercises n ──── 1 exercises
  │                                                                ▲
  │ n                                                              │
users_roles n ──── 1 roles                                         │
  │                                                                │
  └──── n measurements                              createdBy (n..1 users, exercices personnels)
```

| Table | Champs principaux |
|---|---|
| `users` | id, first_name, last_name, email (unique), password, birth_date, gender, height_cm, target_weight_kg, weekly_goal, activity_level, created_at |
| `roles` | id, name |
| `users_roles` | user_id, role_id |
| `exercises` | id, name, muscle_group, description, met, created_by (null = officiel) |
| `workouts` | id, user_id, date, type, duration_minutes, intensity, calories, notes, created_at |
| `workout_exercises` | id, workout_id, exercise_id, position, sets, reps, weight_kg, duration_seconds, distance_km, comment |
| `measurements` | id, user_id, date, weight_kg, body_fat_pct, waist_cm, chest_cm, notes |

## Calculs métier

| Indicateur | Formule |
|---|---|
| Calories d'une séance | `MET(type) × facteur(intensité) × 3,5 × poids(kg) / 200 × durée(min)` |
| Volume soulevé | `Σ (séries × répétitions × charge)` |
| IMC | `poids / taille(m)²` |
| Métabolisme de base | Mifflin-St Jeor : `10 × poids + 6,25 × taille − 5 × âge + (5 ou −161)` |
| Dépense totale (TDEE) | `MB × facteur d'activité` (1,2 à 1,725) |
| Série hebdomadaire | Semaines consécutives (lundi → dimanche) contenant au moins une séance |

Valeurs MET par type : musculation 5,0 · cardio 8,0 · HIIT 9,0 · mobilité 3,0 · sport 7,0.
Facteurs d'intensité : faible 0,85 · modérée 1,0 · élevée 1,2.

## API REST

Endpoint JSON en lecture seule (session authentifiée requise) :

```http
GET /api/stats/summary
```

```json
{
  "athlete": "Alex Martin",
  "week": { "sessions": 4, "goal": 4, "minutes": 225, "calories": 2197 },
  "streakWeeks": 10,
  "totalSessions": 40,
  "totalMinutes": 2385,
  "latestWeightKg": 80.2,
  "bmi": 25.3,
  "dailyMinutes": [{ "label": "lun.", "minutes": 45 }],
  "weeklySessions": [{ "week": "08/09", "sessions": 4 }]
}
```

## Tests

```bash
mvn test
```

12 tests couvrent les calculs métier et l'intégration web :

- `FitnessCalculatorTest` : formule calorique, intensité, IMC, métabolisme de base, série hebdomadaire (7 tests).
- `FitTrackApplicationTests` : chargement du contexte, données de démo, page publique, redirection des pages protégées,
  rendu du tableau de bord authentifié, réponse JSON de l'API (5 tests).

## Sécurité

- Mots de passe **BCrypt** (coût par défaut), jamais exposés dans les vues.
- **CSRF** activé sur tous les formulaires (injection automatique par Thymeleaf).
- Autorisation par ressource : chaque requête est filtrée par l'utilisateur courant (`findByIdAndUser`).
- Pages statiques et publiques limitées au strict nécessaire, en-têtes `X-Frame-Options: SAMEORIGIN`.
- La console H2 est réservée au développement (désactivée par le profil MySQL).

## Déploiement

```bash
mvn clean package
java -jar target/fittrack-1.0.0.jar --server.port=8080
```

Recommandations pour la production :
- passer en profil MySQL/PostgreSQL et désactiver `spring.h2.console.enabled` ;
- servir l'application derrière HTTPS (reverse proxy) et définir `server.forward-headers-strategy=framework` ;
- restreindre `app.seed.enabled=false` pour ne pas créer le compte de démonstration ;
- sauvegarder la base selon votre SGBD.

## Licence

MIT — projet conçu par **Alex Alkhatib** (Activity Management System).
