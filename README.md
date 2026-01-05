# Pay My Buddy

## Description
Pay My Buddy is a web application that allows users to manage their finances, send money to friends, and keep track of their transactions. The application is built using the Spring Boot framework and follows the MVC (Model-View-Controller) architecture.

## Installation

1. Installer PostgreSQL sur votre machine.

2. Créer une fichier .env à la racine du projet avec les variables d'environnement suivantes :
   - PGUSER : Nom d'utilisateur PostgreSQL avec droits de lecture et d'écriture sur la base de données
   - PGPASSWORD : Mot de passe de l'utilisateur PostgreSQL
   - PGDBNAME : Nom de la base de données PostgreSQL par défaut (ex: postgres)

3. Lancer la commande  : 
```bash
.\run_database_setup.ps1
``` 
 pour créer la base de données et les tables nécessaires.


## Sauvegarde et Restauration de la base de données
### Sauvegarde
Pour sauvegarder la base de données, exécutez la commande suivante dans le terminal :

```bash 
.\run_database_save.ps1
```
Cela crée un fichier de sauvegarde au format .sql dans le répertoire `backups`.

### Restauration
Pour restaurer la base de données à partir d'une sauvegarde, exécutez la commande suivante dans le terminal :

```bash
.\run_database_restore.ps1
```
Cela restaure la base de données à partir du fichier de sauvegarde spécifié.


## Modèle Physique De Données

### Table users : 
| Nom de la colonne | Type de données | Description                                        |
|-------------------|-----------------|----------------------------------------------------|
| id                | BIGINT          | Identifiant unique de l'utilisateur (clé primaire) |
| username          | VARCHAR(255)    | Pseudonyme de l'utilisateur (unique)               |
| email             | VARCHAR(255)    | Adresse e-mail de l'utilisateur (unique)           |
| password          | VARCHAR(255)    | Mot de passe de l'utilisateur                      |

### Table transactions :
| Nom de la colonne | Type de données | Description                                                                    |
|-------------------|-----------------|--------------------------------------------------------------------------------|
| id                | BIGINT          | Identifiant unique de la transaction (clé primaire)                            |
| sender_id         | BIGINT          | Identifiant de l'utilisateur qui envoie de l'argent (clé étrangère vers users) |
| receiver_id       | BIGINT          | Identifiant de l'utilisateur qui reçoit de l'argent (clé étrangère vers users) |
| description       | VARCHAR(255)    | Description de la transaction                                                  |
| amount            | DECIMAL(10, 2)  | Montant de la transaction                                                      |

### Table connections : 
| Nom de la colonne | Type de données | Description                                                                    |
|-------------------|-----------------|--------------------------------------------------------------------------------|
| id                | BIGINT          | Identifiant unique de la connexion (clé primaire)                              |
| user_id           | BIGINT          | Identifiant de l'utilisateur connecté (clé étrangère vers users)               |
| connection_id     | BIGINT          | Identifiant de l'utilisateur connecté (clé étrangère vers users)               |

#### Contraintes :
- La combinaison (user_id, connection_id) doit être unique.