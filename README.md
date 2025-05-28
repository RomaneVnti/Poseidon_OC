# 💼 Poseidon Capital Solutions

Application web Java Spring Boot pour la gestion de données financières internes (bids, ratings, trades, etc.) avec authentification sécurisée.
## 🔧 Technologies utilisées

Java 21

Spring Boot 3.1.0

Spring Security

Spring Data JPA

Hibernate Validator

MySQL

Maven

Thymeleaf

JUnit 5

JaCoCo


## 🚀 Installation

### 1. Cloner le projet
```bash
git clone https://github.com/RomaneVnti/Poseidon_OC.git
cd Poseidon_OC
```

### 2. Créer la base de données MySQL
   Avant de lancer l'application, il est nécessaire de créer une base de données sur ton serveur MySQL local. Exécute la commande suivante pour créer la base de données :
```bash
CREATE DATABASE demo;
```

### 3. Créer le fichier application.properties
```bash
cp src/main/resources/application-example.properties src/main/resources/application.properties
```
Puis ouvre src/main/resources/application.properties et modifie les informations suivantes :
```bash
spring.datasource.url=jdbc:mysql://localhost:3306/demo
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### 4. Installer les dépendances
```bash
mvn clean install
```

### 5. Lancer l'application
```bash
mvn spring-boot:run
```

## 📝 Utilisation
Une fois l'application lancée, tu peux créer un compte en accédant à la route suivante dans ton navigateur :

Route principale pour la création de compte : http://localhost:8080/app/login

## 🧪 Tests
```bash
mvn clean verify
```

Un rapport JaCoCo sera généré dans target/site/jacoco/index.html pour la couverture des tests.

