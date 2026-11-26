# Module 7: Make a call resilient (take-home, workbook 7.3)

**File:** `backend/src/main/java/labs/m7/RatesService.java`

`GET /api/v1/rates/{currency}` calls an external partner API with no timeouts, no rate limit, no circuit breaker and no fallback. A slow partner ties up a request thread for as long as it likes.

## Task

Wrap the partner call with a Resilience4j rate limiter, bulkhead and circuit breaker, add a fallback that returns the last good (cached) value, and add connect and read timeouts to the HTTP client (Section 7.2 and solution S-7).

The Resilience4j and AspectJ starters are already in `pom.xml`. Put the Resilience4j settings in `application.yml`. The partner's base URL comes from the `app.rates.base-url` property; keep using it.

## Check your work

```bash
./lab-check.sh m7
```

The checks start a fake partner on `127.0.0.1`. They make it slow (8 seconds) and expect your API to answer within 5 seconds with the cached rate. Then they make it fail and expect fewer than 120 of 150 requests to reach it.
