# Module 5: Harden a workflow (workbook 5.4)

**File:** `.github/workflows/legacy-build.yml`

This is the vulnerable workflow from Section 5.1. It never runs: its job only runs in a repository named `lab-safety/this-workflow-never-runs`, which does not exist. Do not remove that `if:` line.

## Tasks

1. **Task 1.** Lint it and list every finding:

   ```bash
   actionlint
   zizmor --persona=auditor .github/workflows/legacy-build.yml
   ```

   Use `--persona=auditor`: the default persona hides some findings, including the `write-all` permissions and the self-hosted runner.
2. **Task 2.** Rewrite it as the two hardened workflows from Section 5.1 (`ci.yml` and `deploy.yml`). Look up the current commit SHA for every action you use. `.github/workflows/build.yml` is a worked example.
   Your fork has no self-hosted runner, so guard the deploy job (for example `if: vars.ENABLE_ONPREM_DEPLOY == 'true'`), or it will sit in the queue for 24 hours.
3. **Task 3.** Re-run both tools until they are clean. actionlint warns about the custom `onprem-deploy` runner label; declare it in `.github/actionlint.yaml`.
4. **Discussion.** Where does your deploy runner sit in the network, and exactly which hosts and ports does it need?

Note: `actions/checkout` v7 and later refuse to check out fork pull-request code under `pull_request_target` unless you set `allow-unsafe-pr-checkout: true`. The legacy workflow uses v6, like many real repositories still do.
