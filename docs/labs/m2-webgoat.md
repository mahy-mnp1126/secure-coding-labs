# Module 2: Exploitation with WebGoat (workbook 2.1 – 2.4)

There is no code to change in this repository for Module 2. Use WebGoat from Day 0:

```bash
docker run --rm -it -p 127.0.0.1:8080:8080 -p 127.0.0.1:9090:9090 -e TZ=America/Toronto webgoat/webgoat
```

Open http://127.0.0.1:8080/WebGoat and work through the lessons listed in the workbook: SQL Injection (intro and advanced), Cross Site Scripting, the JWT lessons (instructor demo) and Server-Side Request Forgery (instructor demo).

Then fill in the attack-to-defence map (workbook 2.4) using the **OWASP Top 10:2025** categories.

Attack only your local WebGoat. Never point these payloads at anything else.
