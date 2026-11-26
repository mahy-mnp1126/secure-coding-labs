# Module 6: Fix the logs (workbook 6.4)

**Files:** `backend/src/main/java/labs/m6/AccountService.java` and `AccountController.java`

Support staff call `POST /api/v1/customers/{customerId}/address`. The service logs the whole request (a record's `toString()` prints every field, including the SIN), logs the e-mail, SIN and bearer token on failure, and writes no audit event.

## Try it

With Keycloak running, get a token for `support` (see the main README), then:

```bash
curl -X POST -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" \
  -d '{"email":"jane.doe@example.com","sin":"046 454 286","newAddress":"1 Test Rd"}' \
  http://127.0.0.1:8081/api/v1/customers/C-1001/address
```

Look at the API's console output.

## Tasks

1. **Task 1.** Remove or mask everything in the "never log" column (Section 6.1).
2. **Task 2.** Add an audit event for address changes, on success and on failure, from a logger named `AUDIT`, with the actor, the subject, the outcome and the correlation id. The correlation id is already added to every log line by `labs.common.CorrelationIdFilter` (you will see `cid=...` in the output), so you only need the first three in your message:

   ```
   event=customer.address.update actor=<caller id> subject=<customer id> outcome=success|failure
   ```

3. **Task 3 (take-home).** Propose one alert rule the SOC should build from these events.

## Check your work

```bash
./lab-check.sh m6
```

The checks capture the console output and fail if the SIN, e-mail, token or new address appears in it.
