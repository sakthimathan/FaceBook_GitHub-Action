<#
Setup script for a self-hosted GitHub Actions runner on Windows (x64).

Usage (PowerShell):
  # Provide a GitHub PAT with repo or admin:org scope in env var GITHUB_PAT or pass as -Pat
  .\setup-windows-runner.ps1 -RepoUrl "https://github.com/<owner>/<repo>" -RunnerName "my-windows-runner" -Labels "self-hosted,windows" -WorkDir "C:\actions-runner"

Parameters:
  -RepoUrl   : Repository URL (https://github.com/owner/repo) OR Organization URL (https://github.com/org)
  -Pat       : GitHub Personal Access Token (optional; fallback to env GITHUB_PAT)
  -RunnerName: Name to assign to runner (optional; default: machine hostname)
  -Labels    : Comma-separated labels for the runner (optional)
  -WorkDir   : Directory to install runner files (optional; default C:\actions-runner)

Notes & prerequisites:
  - PAT must have repo (for repo-level runner) or admin:org (for organization-level) permissions.
  - This script runs on Windows x64. For Linux, use the official instructions: https://docs.github.com/en/actions/hosting-your-own-runners
  - The script will install the runner as a Windows service. Run PowerShell as Administrator.
  - Keep the PAT secret; do not commit it to the repo.
#>

param(
    [Parameter(Mandatory=$true)]
    [string]$RepoUrl,

    [string]$Pat = $env:GITHUB_PAT,

    [string]$RunnerName = $env:COMPUTERNAME,

    [string]$Labels = "self-hosted,windows",

    [string]$WorkDir = "C:\actions-runner"
)

function Write-Log { param($m) Write-Host "[setup-runner] $m" }

if (-not $Pat) {
    Write-Error "No PAT provided. Set environment variable GITHUB_PAT or pass -Pat. The PAT needs repo or admin:org scope depending on repo vs org runner."
    exit 1
}

# Create work dir
if (-not (Test-Path -Path $WorkDir)) {
    New-Item -ItemType Directory -Path $WorkDir -Force | Out-Null
}
Set-Location -Path $WorkDir

# Detect latest runner release and download appropriate asset
$releasesApi = "https://api.github.com/repos/actions/runner/releases/latest"
Write-Log "Querying GitHub releases: $releasesApi"
try {
    $release = Invoke-RestMethod -Method Get -Uri $releasesApi -Headers @{ 'User-Agent' = 'setup-runner-script' }
} catch {
    Write-Error "Failed to fetch runner release info: $_"
    exit 2
}

# Find Windows x64 asset
$asset = $release.assets | Where-Object { $_.name -match 'actions-runner-win-x64-.*\\.zip' } | Select-Object -First 1
if (-not $asset) {
    Write-Error "Could not find Windows x64 runner asset in release."
    exit 3
}

$zipUrl = $asset.browser_download_url
$zipName = Split-Path -Path $zipUrl -Leaf
Write-Log "Downloading runner package: $zipName"

$zipPath = Join-Path $WorkDir $zipName
try {
    Invoke-WebRequest -Uri $zipUrl -OutFile $zipPath -UseBasicParsing
} catch {
    Write-Error "Download failed: $_"
    exit 4
}

# Extract
Write-Log "Extracting $zipPath"
try {
    Expand-Archive -Path $zipPath -DestinationPath $WorkDir -Force
} catch {
    Write-Error "Extraction failed: $_"
    exit 5
}

# Determine token endpoint: repo vs org
# RepoUrl expected forms: https://github.com/owner/repo  OR  https://github.com/org
$uri = [uri]$RepoUrl
$segments = $uri.Segments | ForEach-Object { $_.Trim('/') } | Where-Object { $_ -ne '' }
$apiTokenUrl = $null
if ($segments.Length -eq 1) {
    # organization-level: POST /orgs/{org}/actions/runners/registration-token
    $org = $segments[0]
    $apiTokenUrl = "https://api.github.com/orgs/$org/actions/runners/registration-token"
    Write-Log "Detected org URL, using org registration token endpoint"
} elseif ($segments.Length -ge 2) {
    # repo-level: POST /repos/{owner}/{repo}/actions/runners/registration-token
    $owner = $segments[0]
    $repo = $segments[1]
    $apiTokenUrl = "https://api.github.com/repos/$owner/$repo/actions/runners/registration-token"
    Write-Log "Detected repo URL, using repo registration token endpoint"
} else {
    Write-Error "Could not parse RepoUrl. Provide https://github.com/<owner>/<repo> or https://github.com/<org>"
    exit 6
}

# Request registration token
Write-Log "Requesting registration token from GitHub API"
try {
    $tokenResp = Invoke-RestMethod -Method Post -Uri $apiTokenUrl -Headers @{ Authorization = "token $Pat"; 'User-Agent' = 'setup-runner-script' } -Body @{}
    $regToken = $tokenResp.token
} catch {
    Write-Error "Failed to get registration token. Check PAT permissions and RepoUrl. $_"
    exit 7
}

if (-not $regToken) {
    Write-Error "Registration token missing in response"
    exit 8
}

Write-Log "Configuring runner (unattended). This will register the runner to $RepoUrl with labels: $Labels"

# Run config.cmd
$runnerRoot = Get-ChildItem -Directory -Path $WorkDir | Where-Object { Test-Path (Join-Path $_.FullName 'config.cmd') } | Select-Object -First 1
if (-not $runnerRoot) {
    # runner files may be extracted directly into $WorkDir
    $runnerRoot = Get-Item -Path $WorkDir
}
$cfgCmd = Join-Path $runnerRoot.FullName 'config.cmd'

$cfgArgs = @(
    '--unattended',
    '--url', $RepoUrl,
    '--token', $regToken,
    '--name', $RunnerName
)
if ($Labels) { $cfgArgs += @('--labels', $Labels) }

Write-Log "Running config: $cfgCmd $($cfgArgs -join ' ')"
try {
    & $cfgCmd $cfgArgs
} catch {
    Write-Error "Runner configuration failed: $_"
    exit 9
}

# Install and start service
$svcCmd = Join-Path $runnerRoot.FullName 'svc.cmd'
if (Test-Path $svcCmd) {
    Write-Log "Installing runner as a service"
    try {
        & $svcCmd 'install'
        & $svcCmd 'start'
    } catch {
        Write-Error "Failed to install/start service: $_"
        exit 10
    }
} else {
    Write-Log "svc.cmd not found; you can run the runner interactively: .\run.cmd"
}

Write-Log "Runner installed and started (if service installation succeeded)."
Write-Log "If you need to remove the runner later: run .\config.cmd remove --unattended --token <remove-token> or use GitHub UI to remove the runner."

Write-Host "\nDone. Check your repository/org -> Settings -> Actions -> Runners to confirm the runner is online."
