# Read Club-Web/.env and start the backend
# Usage: .\scripts\run-backend.ps1
$ErrorActionPreference = "Stop"

$envFile = Join-Path $PSScriptRoot "..\Club-Web\.env"
if (-not (Test-Path $envFile)) {
    Write-Host "Club-Web/.env not found. Copy Club-Web/.env.example and fill in JWT_SECRET / DB_PASSWORD." -ForegroundColor Yellow
    exit 1
}

Get-Content $envFile -Encoding utf8 | ForEach-Object {
    $line = $_.Trim()
    if ($line -eq "" -or $line.StartsWith("#")) { return }
    $idx = $line.IndexOf("=")
    if ($idx -le 0) { return }
    $key = $line.Substring(0, $idx).Trim()
    $value = $line.Substring($idx + 1).Trim()
    [Environment]::SetEnvironmentVariable($key, $value, "Process")
}

if (-not $env:JWT_SECRET -or $env:JWT_SECRET.Length -lt 32) {
    Write-Host "JWT_SECRET missing or shorter than 32 bytes. Set it in Club-Web/.env first." -ForegroundColor Yellow
    exit 1
}

Push-Location (Join-Path $PSScriptRoot "..\Club-Web")
try {
    & .\mvnw.cmd spring-boot:run
} finally {
    Pop-Location
}
