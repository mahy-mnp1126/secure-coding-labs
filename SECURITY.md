# Security policy

This repository is a **deliberately vulnerable training application**. The SQL injection, SSRF, access-control, upload, logging and workflow weaknesses in `backend/src/main/java/labs` and `.github/workflows/legacy-build.yml` are intentional, so please do not report them.

If you find a problem that could harm someone who runs the labs as instructed (for example, the app listening on a network interface, or a real credential in the repository), contact the workshop instructor or open an issue without technical details.
