# **Activity Management System — Application d’accompagnement d’activités physiques**

**Activity Management System** est une application Web JEE permettant d’accompagner des utilisateurs dans leurs activités physiques et thérapeutiques.
L’objectif est de proposer un système simple et intuitif pour **suivre**, **noter**, et **gérer des activités personnalisées**, tout en garantissant une architecture professionnelle basée sur **Spring Boot**, **MVC**, et une base de données relationnelle.

Ce projet a été conçu dans un cadre **personnel**, afin de renforcer mes compétences en développement backend Java / Spring.


## 🎯 Objectifs du projet

* Concevoir une application Web permettant d’accompagner des activités thérapeutiques
* Assurer un suivi personnalisé des activités et des recommandations
* Mettre en place une architecture professionnelle (Spring Boot, MVC.2)
* Implémenter des fonctionnalités essentielles : inscription, connexion, gestion des activités
* Maîtriser une stack complète : Spring Boot, Spring Security, Spring Data JPA, Thymeleaf, MySQL


## 📱 Fonctionnalités principales

### 🔐 Authentification & Sécurité

* Inscription d’un utilisateur (création de compte)
* Connexion sécurisée via **Spring Security**
* Gestion des rôles (User / Admin)

### 📝 Gestion des activités thérapeutiques

* Sélection d’activités personnalisées
* Évaluation des activités
* Suppression des activités sélectionnées
* Affichage dynamique via un carrousel + recherche

### 👤 Espace utilisateur

* Vue personnalisée des activités
* Gestion de son propre profil (via rôles et permissions)


## 🧰 Stack Technique

### Backend

* **Spring Boot** (framework principal)
* **Architecture MVC.2** (un contrôleur unique pour l’ensemble des routes)
* **Spring Data JPA** : gestion des entités et repositories
* **Spring Security** : sécurité, login, gestion des accès
* **Thymeleaf** : moteur de templates pour les vues HTML

### Base de données

* **MySQL**
* Modèle relationnel conçu via Draw.io
* Tables : User, Role, User-Role, Activity, User-Activity 
* Mapping via JPA + Repositories Spring Data

### Outils

* IntelliJ IDEA
* Spring Initializr (configuration des dépendances)


## 🧠 Compétences démontrées

✔ Conception et développement d’une application Web JEE moderne
✔ Architecture Spring Boot + MVC maîtrisée
✔ Sécurisation avancée (Spring Security)
✔ Gestion d’une base de données relationnelle (MySQL + JPA)
✔ Création d’interfaces dynamiques avec Thymeleaf
✔ Gestion des entités, repositories, services et contrôleurs
✔ Compréhension des enjeux d’un accompagnement thérapeutique via des activités personnalisées


## 📂 Structure du projet

```
Activity-Management-System/
 ├── src/
 │   ├── main/
 │   │   ├── java/.../controller/       # Contrôleur principal (MVC.2)
 │   │   ├── java/.../model/            # Entités JPA (User, Activity…)
 │   │   ├── java/.../repository/       # Repositories Spring Data
 │   │   ├── java/.../security/         # Config Spring Security
 │   │   ├── resources/templates/        # Vues Thymeleaf (HTML)
 │   │   ├── resources/static/           # CSS, JS, images
 │   │   └── resources/application.properties
 ├── videos/                             # Vidéo de démonstration (Drive)
 ├── Compte Rendu.pdf                    # Documentation de conception :contentReference[oaicite:2]{index=2}
 └── README.md
```


## 🚀 Installation & Lancement

### 1️⃣ Pré-requis

* Java 17+
* Maven
* MySQL / MySQL Workbench

### 2️⃣ Préparer la base de données

1. Lancer MySQL
2. Créer un schéma :

```sql
CREATE DATABASE ams;
```

### 3️⃣ Lancer l’application

Depuis IntelliJ ou via Maven :

```bash
mvn spring-boot:run
```

Par défaut, l’application tourne sur :

👉 **[http://localhost:8080](http://localhost:8080)**


## 🎥 Démonstration

La vidéo de présentation du projet est disponible ici :

➡️ *(Lien Drive fourni dans ton PDF)*
[https://drive.google.com/drive/folders/1sENwrH_BYc2Rcg13-rL8HxGt7vTFR2O2?usp=sharing](https://drive.google.com/drive/folders/1sENwrH_BYc2Rcg13-rL8HxGt7vTFR2O2?usp=sharing)


## 🔧 Pistes d’amélioration

* Système de recommandations d’activités basé sur le profil utilisateur
* Ajout d’un score ou d’un suivi d’évolution des activités
* API REST pour future application mobile
* Tableau de bord administrateur
* Refonte UI avec un framework moderne (Vue.js / React)
* Ajout des tests unitaires (JUnit / Mockito)


## 👤 À propos

Développeur Java / Spring passionné, je réalise ce type de projets pour monter en compétences full-stack et comprendre les enjeux réels d’applications JEE robustes.
Profil : **[GitHub](https://github.com/AlexAlkhatib)**


## 📄 Licence

MIT License  Copyright (c) 2025 Alex Alkhatib
