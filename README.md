# Co-Banking Backend

Production-ready Spring Boot 3.x backend for the Co-Banking React app.

## Tech Stack
- Java 21
- Spring Boot 3.3.x
- Spring Security + JWT
- Spring Data JPA
- MySQL
- Flyway
- Swagger / OpenAPI
- Google Sign-In token verification

## Project Structure
- `auth` – register/login/logout/google sign-in
- `accounts` – account listing and balances
- `transactions` – transfers and transaction history
- `bills` – bill listing and payment
- `investments` – portfolio, buy, sell
- `notifications` – list, mark read, delete
- `common` – exceptions and shared concerns

## Run locally
1. Create MySQL database:
   - `co_banking_db`
   - `co_banking_dev`
2. Update `src/main/resources/application.yml` if needed.
3. Start the app:

```bash
mvn spring-boot:run
```

## Profiles
- `dev` – local development, `create-drop` schema mode
- `test` – H2 in-memory database
- `prod` – environment-variable driven configuration

## Swagger UI
After startup:
- `http://localhost:8080/api/swagger-ui.html`

## Health endpoint
- `http://localhost:8080/api/actuator/health`

## Frontend access
- Local React dev origins are allowed for `http://localhost:3000` and `http://localhost:5173`.
- Send JWTs as `Authorization: Bearer <token>`.

## Base API routes
All routes are under `/api`.

### Auth
- `POST /auth/register`
- `POST /auth/login`
- `POST /auth/logout`
- `POST /auth/google`

### Accounts
- `GET /accounts`
- `GET /accounts/{id}`
- `GET /accounts/{id}/balance`

### Transactions
- `GET /transactions/{accountId}`
- `POST /transactions/transfer`

### Bills
- `GET /bills`
- `POST /bills/pay`

### Investments
- `GET /investments/portfolio`
- `POST /investments/buy`
- `POST /investments/sell/{id}`

### Notifications
- `GET /notifications`
- `PUT /notifications/{id}/read`
- `DELETE /notifications/{id}`

## Example curl
### Register
```bash
curl -X POST http://localhost:8080/api/auth/register ^
  -H "Content-Type: application/json" ^
  -d "{\"email\":\"john@example.com\",\"password\":\"Password123!\",\"firstName\":\"John\",\"lastName\":\"Doe\",\"phone\":\"0712345678\"}"
```

### Login
```bash
curl -X POST http://localhost:8080/api/auth/login ^
  -H "Content-Type: application/json" ^
  -d "{\"email\":\"john@example.com\",\"password\":\"Password123!\"}"
```

### Google sign-in
```bash
curl -X POST http://localhost:8080/api/auth/google ^
  -H "Content-Type: application/json" ^
  -d "{\"idToken\":\"YOUR_GOOGLE_ID_TOKEN\"}"
```

### Transfer
```bash
curl -X POST http://localhost:8080/api/transactions/transfer ^
  -H "Content-Type: application/json" ^
  -H "Authorization: Bearer YOUR_JWT" ^
  -d "{\"fromAccountId\":1,\"toAccountId\":2,\"amount\":100.00,\"description\":\"Rent\"}"
```

## Environment variables for production
- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `JWT_SECRET`
- `GOOGLE_CLIENT_ID`

## Notes
- Transfers and bill payments are transactional.
- Balance checks prevent negative balances.
- Google tokens are verified on the backend using the configured client ID.
- If you use a local frontend proxy, you can narrow or remove the CORS allow-list.
