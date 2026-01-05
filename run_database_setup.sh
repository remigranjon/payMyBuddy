#!/bin/bash

# Charger les variables d'environnement depuis un fichier .env
if [ -f .env ]; then
  export $(grep -v '^#' .env | xargs)
else
  echo "Le fichier .env est introuvable."
  exit 1
fi

# Configuration
PGUSER=$PGUSER
PGPASSWORD=$PGPASSWORD
PGDBNAME=$PGDBNAME
SCRIPT_PATH="create_database.sql"

# Vérification de l'installation de PostgreSQL
if ! command -v psql &> /dev/null; then
  echo "PostgreSQL n'est pas installé ou psql n'est pas dans le PATH."
  exit 1
fi

# Vérification de l'existence du fichier SQL
if [ ! -f "$SCRIPT_PATH" ]; then
  echo "Le fichier $SCRIPT_PATH est introuvable."
  exit 1
fi

# Exécution du script SQL
export PGPASSWORD=$PGPASSWORD
psql -U "$PGUSER" -d "$PGDBNAME" -f "$SCRIPT_PATH"

# Vérification de l'exécution
if [ $? -eq 0 ]; then
  echo "Script SQL exécuté avec succès."
else
  echo "Une erreur s'est produite lors de l'exécution du script."
fi