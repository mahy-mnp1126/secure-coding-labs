#!/usr/bin/env bash
# Run the checks for one lab. They FAIL until you have fixed the lab, then PASS.
#   ./lab-check.sh m1        Module 1, Tasks 2-3 (SQL injection, authentication)
#   ./lab-check.sh m1-ssrf   Module 1, Task 4 (SSRF, take-home)
#   ./lab-check.sh m3        Module 3 (harden an endpoint, file upload)
#   ./lab-check.sh m6        Module 6 (logging and audit)
#   ./lab-check.sh m7        Module 7 (resilience, take-home)
set -euo pipefail
lab="${1:-}"
case "$lab" in
  m1|m1-ssrf|m3|m6|m7) ;;
  *) echo "usage: ./lab-check.sh {m1|m1-ssrf|m3|m6|m7}"; exit 2 ;;
esac
cd "$(dirname "$0")"
./mvnw -q -B test -Dgroups="$lab" -DexcludedGroups=none -Dsurefire.failIfNoSpecifiedTests=false
echo "All $lab checks passed."
