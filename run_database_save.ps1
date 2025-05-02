# Charger les variables d'environnement depuis un fichier .env
Get-Content .env | ForEach-Object {
    if ($_ -match "^(.*?)=(.*)$") {
        [System.Environment]::SetEnvironmentVariable($matches[1], $matches[2])
    }
}

# Configuration
$PGUSER = $env:PGUSER
$PGPASSWORD = $env:PGPASSWORD
$BACKUP_DIR = "backups"
$TIMESTAMP = Get-Date -Format "yyyyMMdd_HHmmss"
$BACKUP_FILE = "$BACKUP_DIR\pay_my_buddy`_$TIMESTAMP.sql"

# Vérification de l'installation de PostgreSQL
if (-not (Get-Command "pg_dump" -ErrorAction SilentlyContinue)) {
    Write-Host "PostgreSQL n'est pas installé ou pg_dump n'est pas dans le PATH."
    exit 1
}

# Création du répertoire de sauvegarde s'il n'existe pas
if (-not (Test-Path $BACKUP_DIR)) {
    New-Item -ItemType Directory -Path $BACKUP_DIR | Out-Null
}

# Exécution de la commande pg_dump
$env:PGUSER = $PGUSER
$env:PGPASSWORD = $PGPASSWORD
$command = "pg_dump -U $PGUSER -d pay_my_buddy -F c -f $BACKUP_FILE"
Invoke-Expression $command

# Vérification de l'exécution
if ($LASTEXITCODE -eq 0) {
    Write-Host "Sauvegarde réussie : $BACKUP_FILE"
} else {
    Write-Host "Une erreur s'est produite lors de la sauvegarde."
}