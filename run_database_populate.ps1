# Charger les variables d'environnement depuis un fichier .env
Get-Content .env | ForEach-Object {
    if ($_ -match "^(.*?)=(.*)$") {
        [System.Environment]::SetEnvironmentVariable($matches[1], $matches[2])
    }
}

# Configuration
$PGUSER = $env:PGUSER
$PGPASSWORD = $env:PGPASSWORD
$SCRIPT_PATH = "populate_database.sql"

# Vérification de l'installation de PostgreSQL
if (-not (Get-Command "psql" -ErrorAction SilentlyContinue)) {
    Write-Host "PostgreSQL n'est pas installé ou psql n'est pas dans le PATH."
    exit 1
}

# Vérification de l'existence du fichier SQL
if (-not (Test-Path $SCRIPT_PATH)) {
    Write-Host "Le fichier $SCRIPT_PATH est introuvable."
    exit 1
}

# Exécution du script SQL
$env:PGUSER = $PGUSER
$env:PGPASSWORD = $PGPASSWORD
$command = "psql -U $PGUSER -d pay_my_buddy -f $SCRIPT_PATH"
Invoke-Expression $command

# Vérification de l'exécution
if ($LASTEXITCODE -eq 0) {
    Write-Host "Script SQL exécuté avec succès."
} else {
    Write-Host "Une erreur s'est produite lors de l'exécution du script."
}