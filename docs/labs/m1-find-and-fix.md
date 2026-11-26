# Module 1: Find and fix (workbook 1.8)

**File:** `backend/src/main/java/labs/m1/SearchController.java`

The product search builds SQL by concatenation, anyone can call it, and the partner-price lookup fetches whatever URL the client sends.

## Try the attacks first

Start the API (`./mvnw spring-boot:run` in `backend`). The search endpoint is public in the seed, so no token is needed.

```bash
# A normal search
curl "http://127.0.0.1:8081/api/v1/products?q=lamp"

# Input: x%' OR 1=1 --     (every product comes back)
curl "http://127.0.0.1:8081/api/v1/products?q=x%25%27%20OR%201%3D1%20--%20"

# The server fetches its own internal endpoint for you (SSRF)
curl "http://127.0.0.1:8081/api/v1/partner-price?url=http://127.0.0.1:8081/actuator/health"
```

## Tasks

1. **Task 1.** Work out a payload that reads the `users` table through the search. The query returns three columns (`id`, `name`, `price`); a UNION must match that shape.
2. **Task 2.** Rewrite the query with bound parameters (`JdbcTemplate` or `PreparedStatement`). Searching for `O'Neil` must still work.
3. **Task 3.** Only authenticated users may search. `labs.config.SecurityConfig` marks the path as public for this lab; `@PreAuthorize` is already enabled.
4. **Task 4 (take-home).** Replace the URL fetch with an allow-listed, no-redirect client (workbook 1.5).

## Check your work

```bash
./lab-check.sh m1         # Tasks 2 and 3
./lab-check.sh m1-ssrf    # Task 4
```

The SSRF check starts a small "internal" service on `127.0.0.1` and fails if your API ever calls it.
