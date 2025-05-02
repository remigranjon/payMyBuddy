# Charger les variables d'environnement depuis un fichier .env
Get-Content .env | ForEach-Object {
    if ($_ -match "^(.*?)=(.*)$") {
        [System.Environment]::SetEnvironmentVariable($matches[1], $matches[2])
    }
}

# Configuration
$PGUSER = $env:PGUSER
$PGPASSWORD = $env:PGPASSWORD

# Demander à l'utilisateur le nom du fichier de sauvegarde
$BACKUP_FILE = Read-Host "Entrez le nom du fichier de sauvegarde (ex: pay_my_buddy_latest.sql)"

# Vérification de l'installation de PostgreSQL
if (-not (Get-Command "pg_restore" -ErrorAction SilentlyContinue)) {
    Write-Host "PostgreSQL n'est pas installé ou pg_restore n'est pas dans le PATH."
    exit 1
}

# Vérification de l'existence du fichier de sauvegarde
if (-not (Test-Path "./backups/$BACKUP_FILE")) {
    Write-Host "Le fichier de sauvegarde spécifié n'existe pas : $BACKUP_FILE"
    exit 1
}

# Exécution de la commande pg_restore
$env:PGUSER = $PGUSER
$env:PGPASSWORD = $PGPASSWORD
$command = "pg_restore -U $PGUSER -d pay_my_buddy -c ./backups/$BACKUP_FILE"
Invoke-Expression $command

# Vérification de l'exécution
if ($LASTEXITCODE -eq 0) {
    Write-Host "Restauration réussie à partir de : $BACKUP_FILE"
} else {
    Write-Host "Une erreur s'est produite lors de la restauration."
}