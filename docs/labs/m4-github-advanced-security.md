# Module 4: GitHub Advanced Security (workbook 4.5)

Everything here happens in **your public fork**. Make sure the security features from Day 0 are on.

## Task 1: CodeQL advanced setup and dependency review

1. In Settings > Advanced Security, open CodeQL analysis and **switch from Default to Advanced setup**. If you add the workflow while Default setup is still on, GitHub rejects the upload.
2. Add `.github/workflows/codeql.yml` and `.github/workflows/dependency-review.yml` from workbook Sections 4.1 and 4.2. Pin each action to a full commit SHA, as in `.github/workflows/build.yml`.
3. Create a branch ruleset for `main` (Settings > Rules > Rulesets): require a pull request, require code scanning results (CodeQL, High or higher), and require the `dependency-review` check.

## Task 2: Watch CodeQL block a pull request

`backend/src/main/java/labs/m4/UserLookupService.java` contains the **safe** `findUser` from Section 1.3. In a new branch, replace it with the **vulnerable** version from Section 1.3 (string concatenation and `Statement`), push, and open a pull request against your fork's `main`.

Expected: CodeQL reports "Query built from user-controlled sources" with the path from `@RequestParam username` in `UserLookupController` to `executeQuery`, and the ruleset blocks the merge. Compare the Copilot Autofix suggestion with Section 1.3.

## Task 3 (take-home): Dependabot

Add `.github/dependabot.yml` (Section 4.2). In a branch, downgrade one dependency in `backend/pom.xml` to a version with a known advisory and watch dependency review fail.

## Task 4: Push protection

Create a fine-grained GitHub token with **no permissions** and a 1-day expiry, put it in a file, commit, and push. Read the message, then delete the token on GitHub (Settings > Developer settings).

## Task 5: Triage

Your instructor will give you three seeded alerts. For each one, decide: fix, or dismiss with the correct reason and a comment.
