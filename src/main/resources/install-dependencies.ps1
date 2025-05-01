# Script pour installer les dépendances JavaFX nécessaires
# Exécuter ce script en tant qu'administrateur

Write-Host "Vérification des dépendances pour Fermista-java..." -ForegroundColor Cyan

# Vérifier si Maven est installé
$mavenInstalled = $false
try {
    $mvnVersion = & mvn -version
    $mavenInstalled = $true
    Write-Host "Maven est installé: $mvnVersion" -ForegroundColor Green
} catch {
    Write-Host "Maven n'est pas installé ou n'est pas dans le PATH." -ForegroundColor Red
    Write-Host "Instructions pour installer Maven:" -ForegroundColor Yellow
    Write-Host "1. Téléchargez Maven depuis https://maven.apache.org/download.cgi" -ForegroundColor Yellow
    Write-Host "2. Décompressez l'archive dans un dossier (par exemple C:\maven)" -ForegroundColor Yellow
    Write-Host "3. Ajoutez le chemin du dossier bin (par exemple C:\maven\bin) à la variable d'environnement PATH" -ForegroundColor Yellow
}

# Vérifier si JavaFX est installé
Write-Host "`nVérification des modules JavaFX..." -ForegroundColor Cyan

$javaFXModules = @(
    "javafx-controls",
    "javafx-fxml",
    "javafx-web",
    "javafx-swing"
)

foreach ($module in $javaFXModules) {
    Write-Host "Module $module: " -NoNewline
    
    # Créer un chemin vers le module dans le répertoire .m2
    $userProfile = $env:USERPROFILE
    $moduleDir = "$userProfile\.m2\repository\org\openjfx\$module\21.0.6"
    
    if (Test-Path $moduleDir) {
        Write-Host "Installé" -ForegroundColor Green
    } else {
        Write-Host "Non installé" -ForegroundColor Red
    }
}

# Instructions pour installation manuelle
Write-Host "`nPour installer manuellement les dépendances JavaFX Web:" -ForegroundColor Yellow
Write-Host "1. Téléchargez le SDK JavaFX depuis https://gluonhq.com/products/javafx/" -ForegroundColor Yellow
Write-Host "2. Décompressez l'archive dans un dossier (par exemple C:\javafx-sdk)" -ForegroundColor Yellow
Write-Host "3. Ajoutez les librairies à votre projet en modifiant les propriétés du projet" -ForegroundColor Yellow
Write-Host "4. Ajoutez --module-path C:\javafx-sdk\lib --add-modules=javafx.controls,javafx.fxml,javafx.web aux VM arguments" -ForegroundColor Yellow

# Comment exécuter le projet avec WebView
Write-Host "`nPour exécuter le projet avec JavaFX WebView:" -ForegroundColor Cyan
Write-Host "java --module-path C:\chemin\vers\javafx-sdk\lib --add-modules=javafx.controls,javafx.fxml,javafx.web -jar votre-application.jar" -ForegroundColor White

# Offrir une solution alternative pour sélectionner la localisation
Write-Host "`nSi vous ne pouvez pas utiliser la carte interactive, vous pouvez toujours:" -ForegroundColor Cyan
Write-Host "1. Saisir manuellement votre adresse dans le champ de localisation" -ForegroundColor White
Write-Host "2. Utiliser la liste déroulante des régions comme alternative" -ForegroundColor White

Write-Host "`nFin de la vérification des dépendances." -ForegroundColor Cyan 