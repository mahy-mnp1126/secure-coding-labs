# Run the checks for one lab on Windows. They FAIL until you have fixed the lab, then PASS.
#   .\lab-check.ps1 m1   |  m1-ssrf  |  m3  |  m6  |  m7
param([Parameter(Mandatory = $true)][ValidateSet("m1", "m1-ssrf", "m3", "m6", "m7")][string]$Lab)
Set-Location $PSScriptRoot
.\mvnw.cmd -q -B test "-Dgroups=$Lab" "-DexcludedGroups=none" "-Dsurefire.failIfNoSpecifiedTests=false"
if ($LASTEXITCODE -eq 0) { Write-Host "All $Lab checks passed." } else { exit $LASTEXITCODE }
