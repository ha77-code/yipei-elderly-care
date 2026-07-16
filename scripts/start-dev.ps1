$ErrorActionPreference = 'Stop'

$projectRoot = Split-Path -Parent $PSScriptRoot
$frontendRoot = Join-Path $projectRoot 'frontend'

if (-not (Get-NetTCPConnection -LocalPort 3306 -State Listen -ErrorAction SilentlyContinue)) {
    Write-Warning 'MySQL is not listening on localhost:3306. Start the MySQL80 service before using the application.'
}

$backendCommand = "Set-Location -LiteralPath '$projectRoot'; mvn -q spring-boot:run"
$frontendCommand = "Set-Location -LiteralPath '$frontendRoot'; npm.cmd run serve"

Start-Process -FilePath 'powershell.exe' -ArgumentList '-NoExit', '-Command', $backendCommand
Start-Process -FilePath 'powershell.exe' -ArgumentList '-NoExit', '-Command', $frontendCommand

Write-Host 'Backend:  http://localhost:8080'
Write-Host 'Frontend: http://localhost:3000'
