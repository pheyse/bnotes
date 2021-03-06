# #######################################################################################################################################
# Export Script for Bnotes:
# =========================
#
# Configuration:
# --------------
# Configuration via properties file located the user directory at sub-path: "\.app-config\bnotes-export\main.properties"
# Example content:
#    username=u
#    password=pw
#    host=http://localhost:8080
#    outputDir=C:\\DA1D\\export-test
#
# Execution Steps:
# ----------------
# 1. the web service "signing" is called via curl.exe to retrieve the JWT which is then used for the next calls
# 2. The web service "listExportItems" is called via curl.exe
# 3. For each each export item the web service "exportItem" is called via curl.exe
#    The data is written to the output directory given in the properties file. There a sub-folder based on the current time is created.
# #######################################################################################################################################
$ErrorActionPreference = "Stop"
Write-Host "========================================================================================================================"
Write-Host "Exporting data"
Write-Host "========================================================================================================================"
$propfile = $env:USERPROFILE + "\.app-config\bnotes-export\main.properties"

Write-Host "reading data from properties file: "$propfile
$AppProps = convertfrom-stringdata (get-content $propfile -raw)

$username = $AppProps.'username'
$password = $AppProps.'password'
$hostUrl = $AppProps.'host'
$outputDir = $AppProps.'outputDir'

if (-Not $hostUrl.EndsWith("/")){
  $hostUrl = $hostUrl + "/";
}

if (-Not $outputDir.EndsWith("\")){
  $outputDir = $outputDir + "\";
}

$timeStamp = Get-Date -UFormat '+%Y_%m_%d__%H_%M_%S'
Write-Host "timeStamp: '$timeStamp'"
$outputDir = $outputDir + $timeStamp + "\";

Write-Host "user: '$username'"
Write-Host "password: '$password'"
Write-Host "host: '$hostUrl'"
Write-Host "outputDir: '$outputDir'"

if (-Not(Test-Path "$outputDir")){
  New-Item -ItemType "directory" -Path "$outputDir"
}

# triple quotes are needed on windows
$signinDataJson = '{"""username""":"""' + $username + '""","""password""":"""' + $password + '"""}'
Write-Host "signinDataJson: >>$signinDataJson<<"

$signInUrl = $hostUrl + "users/signin"
Write-Host "signInUrl: '$signInUrl'"

$jwt = curl.exe -X POST --header "Content-Type: application/json" --data $signinDataJson $signInUrl -f
if ($LastExitCode -ne 0) {
  throw "curl.exe sign in (get JWT) failed"
}
Write-Host "JWT: '$jwt'"
$jwtHeader = 'Authorization: Bearer ' + $jwt
Write-Host "JWT header: '$jwtHeader'"

$listEntitiesUrl = $hostUrl + "listExportItems"

Write-Host "listEntitiesUrl: '$listEntitiesUrl'"

$exportItemsJson = curl.exe -X POST --header $jwtHeader $listEntitiesUrl -f
if ($LastExitCode -ne 0) {
  throw "curl.exe to list export items failed"
}
$exitCode = $LastExitCode
Write-Host "exitCode = "$exitCode
Write-Host "exportItemsJson: '$exportItemsJson'"

$exportItemsJson = "{items:" + $exportItemsJson + "}"
Write-Host "exportItemsJson: '$exportItemsJson'"

$exportItems = ConvertFrom-Json -InputObject $exportItemsJson
Write-Host "exportItems: '$exportItems.items'"

$exportItems = $exportItems.items

Write-Host "exportItems: '$exportItems'"

for ($i=0; $i -lt $exportItems.length; $i++){
  $exportEntityUrl = $hostUrl + "exportItem?item=" + $exportItems[$i]
  $itemSubPath = $exportItems[$i].replace("%2F", "\")
  $outputFilePath = $outputDir + $itemSubPath

  $parentFile = Split-Path $outputFilePath -Parent
  $parentFile = $parentFile + "\"

  if (-Not(Test-Path "$parentFile")){
    New-Item -ItemType "directory" -Path "$parentFile"
  }

  Write-Host "---"
  Write-Host "url = "$exportEntityUrl
  Write-Host "parentFile = "$parentFile
  Write-Host "outputFilePath = "$outputFilePath

  curl.exe -X POST --header $jwtHeader $exportEntityUrl -o $outputFilePath -f
  if ($LastExitCode -ne 0) {
    throw "curl.exe to export item '" + $exportItems[$i] + "'failed"
  }

}

Write-Host -ForegroundColor DarkGreen
Write-Host -ForegroundColor DarkGreen
Write-Host "========================================================================================================================"  -ForegroundColor DarkGreen
Write-Host "Exporting data finished successfully."$exportItems.length"item(s) exported to '$outputDir'."  -ForegroundColor DarkGreen
Write-Host "========================================================================================================================"  -ForegroundColor DarkGreen