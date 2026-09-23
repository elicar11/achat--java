# 🛒 Achat
## Application de gestion des achats et des stocks

## Présentation

**Achat** est une application de bureau développée en **Java Swing** permettant de gérer les achats, les fournisseurs, les produits, les commandes, les réceptions et les factures.

L'application repose sur une architecture en couches afin de séparer l'interface utilisateur, la logique métier et l'accès aux données.

### Objectifs

- Gérer les fournisseurs.
- Gérer les produits et leurs stocks.
- Gérer les fournisseurs de type **PERSONNE** ou **SOCIETE**.
- Associer des produits aux fournisseurs avec leurs prix d'achat.
- Créer et gérer les commandes.
- Gérer les réceptions de marchandises.
- Mettre à jour automatiquement le stock lors des réceptions.
- Gérer les factures et leur état de paiement.
- Rechercher et filtrer les données.
- Exporter les factures au format PDF.

---

## Fonctionnalités

###  Fournisseurs

Deux types de fournisseurs sont pris en charge :

**PERSONNE**
- Nom
- Prénom

**SOCIETE**
- Raison sociale
- NIF
- STAT

Informations communes :
- Adresse
- Email
- Téléphone

Opérations :
- Ajouter
- Modifier
- Supprimer
- Rechercher
- Validation des champs

### Produits

Un produit contient notamment :
- Identifiant
- Désignation
- Description
- Stock actuel
- Stock d'alerte

Opérations :
- Ajouter
- Modifier
- Supprimer
- Rechercher
- Contrôler le stock
- Détecter les stocks faibles ou en rupture

### Produits proposés

La relation **PROPOSER** associe :
- un fournisseur ;
- un produit ;
- un prix d'achat spécifique.

Un produit peut donc être proposé par plusieurs fournisseurs avec des prix différents.

Lors de la création d'une commande, le prix correspondant au fournisseur et au produit est récupéré automatiquement.

### Commandes

Une commande contient :
- une date ;
- un fournisseur ;
- un état ;
- une ou plusieurs lignes de commande.

Chaque ligne contient :
- un produit ;
- une quantité ;
- un prix unitaire d'achat.

Le prix appliqué est conservé dans la ligne de commande afin de préserver l'historique.

### Réceptions

Une commande peut être livrée en plusieurs réceptions.

Une réception contient :
- une date ;
- un numéro de bon de livraison ;
- une commande ;
- une ou plusieurs lignes de réception.

L'enregistrement d'une réception augmente automatiquement le stock des produits concernés.

### Factures

Une facture contient notamment :
- Numéro de facture
- Date
- Montant HT
- TVA
- État de paiement

États de paiement :
- `NON_PAYEE`
- `PAYEE`
- `PARTIELLEMENT_PAYEE`

Le montant TTC est calculé automatiquement.

Les factures peuvent être exportées au format **PDF**.

---

#  Architecture

Le projet suit une architecture en couches :

```text
┌─────────────────────────────────────┐
│          Interface graphique        │
│      Java Swing + FlatLaf           │
│            + MigLayout               │
└──────────────────┬──────────────────┘
                   │
                   ▼
┌─────────────────────────────────────┐
│              SERVICE                │
│           Logique métier            │
└──────────────────┬──────────────────┘
                   │
                   ▼
┌─────────────────────────────────────┐
│                DAO                  │
│            JDBC / SQL               │
└──────────────────┬──────────────────┘
                   │
                   ▼
┌─────────────────────────────────────┐
│               SQLite                │
│              achat.db               │
└─────────────────────────────────────┘
```

### Structure

```text
src/main/java/
└── com/achat/
    ├── Achat.java
    ├── database/
    ├── model/
    ├── dao/
    ├── service/
    └── ui/
        ├── MainFrame.java
        ├── DashboardPanel.java
        ├── fournisseur/
        ├── produit/
        ├── proposer/
        ├── commande/
        ├── reception/
        └── facture/
```

---

# Technologies

| Technologie | Utilisation |
|---|---|
| **Java** | Langage principal |
| **Java Swing** | Interface graphique |
| **FlatLaf** | Apparence moderne de Swing |
| **MigLayout** | Gestion des layouts |
| **SQLite** | Base de données locale |
| **JDBC** | Accès à SQLite |
| **Maven** | Gestion du projet et des dépendances |
| **Apache PDFBox** | Génération des factures PDF |
| **FlatLaf Roboto Fonts** | Typographie |
| **NetBeans** | Environnement de développement |

---

# Prérequis

Installer au minimum :

### Java JDK

Vérifier :

```bash
java -version
javac -version
```

### Maven

Vérifier :

```bash
mvn -version
```

### NetBeans

Le projet peut être ouvert directement avec **Apache NetBeans** ou tout IDE compatible Maven.

---

# Installation

## 1. Récupérer le projet

Avec Git :

```bash
git clone https://github.com/elicar11/achat--java.git
cd achat
```

Ou ouvrir directement le dossier du projet.

## 2. Ouvrir dans NetBeans

```text
File → Open Project
```

Sélectionner le dossier contenant :

```text
pom.xml
```

## 3. Installer les dépendances

Dans le terminal :

```bash
mvn clean install
```

Maven téléchargera automatiquement les bibliothèques déclarées dans `pom.xml`.

---

# Lancement

## Depuis NetBeans

Lancer la classe principale :

```text
com.achat.Achat
```

ou utiliser :

```text
Run Project ou F6 dans NetBeans
```

## Avec Maven

Compiler :

```bash
mvn clean compile
```

Construire :

```bash
mvn clean package
```

Puis lancer l'application selon la configuration Maven du projet.

---

Exemple :

1. Un fournisseur propose un produit.
2. Un prix d'achat est défini.
3. Une commande est créée.
4. Le prix fournisseur est récupéré automatiquement.
5. La commande peut être livrée en plusieurs fois.
6. Chaque réception augmente le stock.
7. Une facture est enregistrée.
8. La facture peut être exportée en PDF.

---

# Fichiers importants

| Fichier | Rôle |
|---|---|
| `pom.xml` | Configuration Maven et dépendances |
| `Achat.java` | Point d'entrée |
| `DatabaseConnection.java` | Connexion SQLite |
| `DatabaseInitializer.java` | Initialisation de la base |
| `model/` | Modèles |
| `dao/` | Accès aux données |
| `service/` | Logique métier |
| `ui/` | Interface graphique |

---

# Dépendances principales

Les principales bibliothèques utilisées sont :

```xml
org.xerial:sqlite-jdbc
com.formdev:flatlaf
com.formdev:flatlaf-fonts-roboto
com.formdev:flatlaf-extras
com.miglayout:miglayout-core
com.miglayout:miglayout-swing
org.apache.pdfbox:pdfbox
```

Les versions exactes sont définies dans :

```text
pom.xml
```
---

# Bonnes pratiques

- Utiliser les **Services** pour la logique métier.
- Utiliser les **DAO** pour les opérations SQL.
- Éviter de placer la logique métier directement dans les panneaux Swing.
- Utiliser des transactions pour les opérations multi-tables.
- Conserver le prix historique dans `LIGNE_COMMANDE`.
- Sauvegarder régulièrement `achat.db`.
- Ne pas modifier directement la base pendant l'utilisation normale de l'application.

---