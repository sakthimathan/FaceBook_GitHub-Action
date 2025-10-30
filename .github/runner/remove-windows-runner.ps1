<#
Cleanup script for a self-hosted GitHub Actions runner on Windows.

Usage (PowerShell):
  .\remove-windows-runner.ps1 -RepoUrl "https://github.com/<owner>/<repo>" -Pat "<PAT>" -WorkDir "C:\actions-runner"

This will:
  - request a remove token from the GitHub API
  - run config.cmd remove --unattended --token <removeToken>
  - stop and uninstall the Windows service (svc.cmd stop/uninstall)
  - optionally delete the runner installation directory

Run PowerShell as Administrator to allow service operations.
#>

param(
    [Parameter(Mandatory=$true)]
    [string]$RepoUrl,

    [string]$Pat = $env:GITHUB_PAT,

    [string]$WorkDir = "C:\actions-runner",

    [switch]$DeleteFiles
)

function Write-Log { param($m) Write-Host "[remove-runner] $m" }

if (-not $Pat) {
    Write-Error "No PAT provided. Set environment variable GITHUB_PAT or pass -Pat. The PAT needs repo or admin:org scope depending on repo vs org runner."
    exit 1
}

$uri = [uri]$RepoUrl
$segments = $uri.Segments | ForEach-Object { $_.Trim('/') } | Where-Object { $_ -ne '' }
$apiTokenUrl = $null
if ($segments.Length -eq 1) {
    $org = $segments[0]
    $apiTokenUrl = "https://api.github.com/orgs/$org/actions/runners/remove-token"
    Write-Log "Detected org URL, using org remove-token endpoint"
} elseif ($segments.Length -ge 2) {
    $owner = $segments[0]
    $repo = $segments[1]
    $apiTokenUrl = "https://api.github.com/repos/$owner/$repo/actions/runners/remove-token"
    Write-Log "Detected repo URL, using repo remove-token endpoint"
} else {
    Write-Error "Could not parse RepoUrl. Provide https://github.com/<owner>/<repo> or https://github.com/<org>"
    exit 2
}

try {
    $resp = Invoke-RestMethod -Method Post -Uri $apiTokenUrl -Headers @{ Authorization = "token $Pat"; 'User-Agent' = 'remove-runner-script' } -Body @{}
    $removeToken = $resp.token
} catch {
    Write-Error "Failed to get remove token: $_"
    exit 3
}

if (-not $removeToken) {
    Write-Error "Remove token missing in response"
    exit 4
}

# Locate config.cmd
$runnerRoot = Get-ChildItem -Directory -Path $WorkDir | Where-Object { Test-Path (Join-Path $_.FullName 'config.cmd') } | Select-Object -First 1
if (-not $runnerRoot) {
    $runnerRoot = Get-Item -Path $WorkDir
}
$cfgCmd = Join-Path $runnerRoot.FullName 'config.cmd'
$svcCmd = Join-Path $runnerRoot.FullName 'svc.cmd'

if (Test-Path $cfgCmd) {
    Write-Log "Running: $cfgCmd remove --unattended --token <removeToken>"
    try {
        & $cfgCmd 'remove' '--unattended' '--token' $removeToken
    } catch {
        Write-Error "config.cmd remove failed: $_"
    }
} else {
    Write-Log "config.cmd not found at $cfgCmd"
}

if (Test-Path $svcCmd) {
    try {
        Write-Log "Stopping and uninstalling service"
        & $svcCmd 'stop'
        & $svcCmd 'uninstall'
    } catch {
        Write-Error "svc.cmd operations failed: $_"
    }
} else {
    Write-Log "svc.cmd not found; service may not be installed or runner files are missing"
}

if ($DeleteFiles) {
    try {
        Write-Log "Deleting runner directory: $WorkDir"
        Remove-Item -Path $WorkDir -Recurse -Force
    } catch {
        Write-Error "Failed to delete $WorkDir: $_"
    }
}

Write-Log "Done. Check GitHub UI to confirm runner is removed."
