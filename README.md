# secure-coding-labs

Hands-on labs for the **MNP Secure Coding** workshop (OWASP Top 10:2025, Java / Spring Boot, GitHub Advanced Security).

> [!WARNING]
> **This repository is deliberately vulnerable.** It contains SQL injection, SSRF, broken access control and unsafe logging on purpose. Run it on your own machine only. It listens on `127.0.0.1` and must never be deployed, exposed to a network, or copied into real projects. All data is synthetic.

## What's inside

| Path | What it is |
|---|---|
| `backend/` | Spring Boot 4.2 / Java 21 API with the lab code (port 8081) |
| `backend/src/main/java/labs/m1`, `m3`, `m4`, `m6`, `m7` | The code for each lab (vulnerable seeds, plus a safe starting point for Module 4) |
| `backend/src/main/java/labs/reference` | Safe reference code from the workbook (invoices, admin endpoint) |
| `backend/src/test/java/labs/labchecks` | Lab checks: tests that **fail until you fix a lab**, then pass |
| `docker-compose.yml`, `keycloak/` | Lab identity provider (Keycloak) and an optional PostgreSQL |
| `.github/workflows/build.yml` | Hardened build workflow (a worked example) |
| `.github/workflows/legacy-build.yml` | Deliberately vulnerable workflow for Module 5. It never runs |
| `docs/labs/` | Step-by-step guide for every lab |

## Before the workshop (Day 0)

1. **Install:** JDK 21 (Eclipse Temurin), Git, Podman or Docker Engine, VS Code (CodeQL and SARIF Viewer extensions) or IntelliJ IDEA. Maven is not needed; the repo includes the Maven wrapper (`mvnw`).
   Do not install Docker Desktop on a work laptop unless your organization has a licence for it.
2. **Fork this repository and keep the fork public.** The GitHub security features used in the labs are free on public repositories.
3. **In your fork, go to Settings > Advanced Security** and turn on: Dependency graph, Dependabot alerts, Dependabot security updates, Secret protection (secret scanning), Push protection, and CodeQL analysis (**Default** setup). Then open the **Actions** tab and enable workflows.
4. **Clone your fork and build it:**

   ```bash
   cd backend
   ./mvnw verify            # Windows: mvnw.cmd verify
   ```

   You should see `BUILD SUCCESS` with 8 tests passing.
5. **Start WebGoat** for Module 2:

   ```bash
   docker run --rm -it -p 127.0.0.1:8080:8080 -p 127.0.0.1:9090:9090 -e TZ=America/Toronto webgoat/webgoat
   ```

   Open http://127.0.0.1:8080/WebGoat. Podman users: replace `docker` with `podman` and prefix the image with `docker.io/`.
6. **Install the workflow linters** for Module 5: [actionlint](https://github.com/rhysd/actionlint) and [zizmor](https://docs.zizmor.sh) (`pipx install zizmor`).

Expect CodeQL alerts in your fork's Security tab straight away. The lab seeds are vulnerable on purpose.

## Running the API

```bash
cd backend
./mvnw spring-boot:run
```

The API starts on http://127.0.0.1:8081 with an in-memory database, so no Docker is needed. Check it with `curl http://127.0.0.1:8081/actuator/health`.

Most endpoints need a JWT. To get real tokens, start the lab identity provider from the repository root:

```bash
docker compose up -d keycloak          # Podman: podman compose up -d keycloak
```

Wait about 30 seconds, then get a token (bash):

```bash
TOKEN=$(curl -s -d client_id=labs-cli -d grant_type=password \
  -d username=alice -d password=alice-lab-pass \
  http://127.0.0.1:8180/realms/labs/protocol/openid-connect/token | jq -r .access_token)

curl -H "Authorization: Bearer $TOKEN" http://127.0.0.1:8081/api/v1/invoices/41
```

PowerShell:

```powershell
$TOKEN = (Invoke-RestMethod -Method Post -Uri http://127.0.0.1:8180/realms/labs/protocol/openid-connect/token `
  -Body @{ client_id = "labs-cli"; grant_type = "password"; username = "alice"; password = "alice-lab-pass" }).access_token
Invoke-RestMethod -Headers @{ Authorization = "Bearer $TOKEN" } http://127.0.0.1:8081/api/v1/invoices/41
```

No `jq`? Run the `curl` to the token endpoint on its own and copy the `access_token` value.

| User | Password | Role | Notes |
|---|---|---|---|
| `alice` | `alice-lab-pass` | CUSTOMER | Owns invoice 41 |
| `bob` | `bob-lab-pass` | CUSTOMER | Owns invoice 42 |
| `support` | `support-lab-pass` | SUPPORT | Can change customer addresses (Module 6) |
| `admin` | `admin-lab-pass` | ADMIN | Can call `/api/v1/admin/stats` |

These are lab-only accounts. The password grant used above is fine for a local lab but is not how real applications should sign users in.

To use PostgreSQL instead of the in-memory database: `docker compose up -d`, then `./mvnw spring-boot:run -Dspring-boot.run.profiles=postgres`.

## The labs

| Module | Lab | Where | Check your work |
|---|---|---|---|
| 1 | Find and fix: SQL injection, missing authentication, SSRF | `labs/m1/SearchController.java` | `./lab-check.sh m1` and `./lab-check.sh m1-ssrf` |
| 2 | Exploitation with WebGoat | WebGoat (Day 0, step 5) | Attack-to-defence map in the workbook |
| 3 | Harden an endpoint and a file upload | `labs/m3/ProfileController.java` | `./lab-check.sh m3` |
| 4 | GitHub Advanced Security: CodeQL, Dependabot, push protection, triage | `labs/m4/`, your fork's settings | Pull request checks in your fork |
| 5 | Harden a workflow | `.github/workflows/legacy-build.yml` | actionlint and zizmor |
| 6 | Fix the logs | `labs/m6/AccountService.java` | `./lab-check.sh m6` |
| 7 | Make a call resilient (take-home) | `labs/m7/RatesService.java` | `./lab-check.sh m7` |
| 8 | Review the robot (AI-generated code) | Done live by the instructor | Module 9 checklist |
| 9 | Review a pull request | Pull request `feature/statement-download` in the training repository | Module 9 checklist |

Paths under `labs/` are in `backend/src/main/java/`. Run `lab-check.sh` from the `backend` folder (Windows: `.\lab-check.ps1 m1`). Each lab has a guide in [`docs/labs`](docs/labs/README.md).

## Rules of engagement

- Attack only the local lab API, WebGoat and your own fork. Never point a payload or tool at anything you are not authorized to test.
- Keep everything bound to `127.0.0.1`.
- Never push your organization's code, configuration, secrets or data to your fork.
- Lab checks are excluded from the normal build, so `./mvnw verify` stays green while you work.

## Licence

MIT. See [LICENSE](LICENSE). Synthetic training content only.
