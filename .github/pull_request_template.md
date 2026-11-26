## What this change does

<!-- One or two sentences. -->

## Security review (Module 9 checklist)

- [ ] New input is validated with an allow-list; SQL uses bound parameters
- [ ] Every new endpoint checks the role **and** object ownership on the server
- [ ] Errors fail closed and return generic messages
- [ ] No secrets, tokens or personal information in code, config or logs
- [ ] New dependencies are genuine, maintained and needed
- [ ] Workflow changes keep least-privilege `permissions` and SHA-pinned actions
- [ ] There is a test that fails if the security check is removed

## AI assistance

- [ ] Parts of this change were written with an AI coding assistant, and I have read, understood and tested them
