# Lab guides

| Guide | Workbook section |
|---|---|
| [Module 1: Find and fix](m1-find-and-fix.md) | 1.8 |
| [Module 2: Exploitation with WebGoat](m2-webgoat.md) | 2.1 – 2.4 |
| [Module 3: Harden an endpoint](m3-harden-an-endpoint.md) | 3.7 |
| [Module 4: GitHub Advanced Security](m4-github-advanced-security.md) | 4.5 |
| [Module 5: Harden a workflow](m5-harden-a-workflow.md) | 5.4 |
| [Module 6: Fix the logs](m6-fix-the-logs.md) | 6.4 |
| [Module 7: Make a call resilient (take-home)](m7-resilience.md) | 7.3 |
| [Module 9: Review a pull request](m9-review-a-pull-request.md) | Module 9 |

## How lab checks work

Each code lab has a set of tests in `backend/src/test/java/labs/labchecks`. They describe what "fixed" looks like, so they **fail on the code you start with** and pass once the lab is done. They are left out of the normal build, so `./mvnw verify` stays green.

From the `backend` folder:

```bash
./lab-check.sh m1          # Windows: .\lab-check.ps1 m1
```

A failing check prints what it expected, for example:

```
M1SearchLabCheck.anonymousCannotSearch [anonymous search must be rejected (401 or 403)]
```

Read the check's source if you are stuck: it shows exactly what is being tested, without giving you the fix. Do not change the checks to make them pass.
