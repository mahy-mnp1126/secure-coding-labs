# Module 3: Harden an endpoint (workbook 3.7)

**File:** `backend/src/main/java/labs/m3/ProfileController.java`

The controller takes the user id from the URL (so anyone can edit anyone's profile), accepts any input, builds SQL by concatenation, HTML-encodes values inside its JSON, allows every CORS origin, and saves uploads under the client's file name inside the public web root.

## Tasks

1. **Task 1.** Apply the patterns from Sections 3.1 – 3.4: a Bean Validation DTO, a parameterized update, a role check, and a restrictive CORS policy.
2. **Task 2.** Remove the HTML encoding from the JSON response and explain where encoding should happen instead.
3. **Task 3.** Fix the upload using the checklist in Section 3.5.

## What the lab checks expect

The checks test the fixed API, so build to this contract:

| | Fixed behaviour |
|---|---|
| Update profile | `PUT /api/v1/profile` with JSON `{"displayName": ..., "bio": ...}`. The user comes from the token (`sub`), never from the URL. Role `CUSTOMER` only. Returns `200` with the raw values. |
| Validation | `displayName`: required, at most 60 characters, letters, spaces, `.`, `'` and `-` only. `bio`: at most 280 characters. Invalid input gets `400`. |
| Other users | No endpoint lets one customer change another customer's profile. |
| CORS | Only `https://portal.example.ca` is allowed. |
| Upload | `POST /api/v1/profile/avatar`, multipart field `file`, role `CUSTOMER`. PNG or JPEG only, detected from the file's content (Apache Tika is already on the classpath). Saved under a server-generated name in `app.upload-dir`, never in `app.web-root`. Returns `201` with `{"id": "<stored name>"}`; other content types get `415`. |

## Check your work

```bash
./lab-check.sh m3
```
