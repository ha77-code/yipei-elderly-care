param(
    [string]$DbHost = $env:YIPEI_DB_HOST,
    [string]$DbPort = $env:YIPEI_DB_PORT,
    [string]$DbUser = $env:YIPEI_DB_USERNAME,
    [string]$DbPassword = $env:YIPEI_DB_PASSWORD
)

$ErrorActionPreference = 'Stop'

if ([string]::IsNullOrWhiteSpace($DbHost)) { $DbHost = '127.0.0.1' }
if ([string]::IsNullOrWhiteSpace($DbPort)) { $DbPort = '3306' }
if ([string]::IsNullOrWhiteSpace($DbUser)) { $DbUser = 'root' }
if ([string]::IsNullOrWhiteSpace($DbPassword)) { $DbPassword = '123456' }

$projectRoot = Split-Path -Parent $PSScriptRoot
$bundledMysql = 'C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe'
$mysql = if (Test-Path -LiteralPath $bundledMysql) {
    $bundledMysql
} else {
    (Get-Command mysql -ErrorAction Stop).Source
}

Push-Location $projectRoot
try {
    & $mysql "--host=$DbHost" "--port=$DbPort" "--user=$DbUser" "--password=$DbPassword" --execute="source sql/init.sql"
    if ($LASTEXITCODE -ne 0) {
        throw "Database bootstrap failed with exit code $LASTEXITCODE."
    }
} finally {
    Pop-Location
}

Write-Host 'Database bootstrap complete: yipei schema and test accounts are ready.'
