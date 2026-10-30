# IA Proxy Backend

AI consumption platform with a **rate limiting and quota Proxy** (Spring Boot 3, Java 17).
Software Patterns workshop: Proxy pattern.

## Proxy pattern in this project

```
Client (AIController)
   │  only knows the AIGenerationService interface
   ▼
RateLimitProxyService   → over N requests/minute?   → 429 + Retry-After
   ▼
QuotaProxyService       → monthly quota used up?    → 402 Payment Required
   ▼                       (deducts tokens and records usage)
MockAIGenerationService → Thread.sleep(1200) + random text
```

| Pattern role | Class |
|---|---|
| Subject (common interface) | `service/AIGenerationService` |
| RealSubject (real object) | `service/MockAIGenerationService` |
| Proxy 1 | `proxy/RateLimitProxyService` |
| Proxy 2 | `proxy/QuotaProxyService` |
| Chain assembly | `config/ProxyChainConfig` |

## Plans

| Plan | Requests/minute | Tokens/month |
|---|---|---|
| FREE | 10 | 50,000 |
| PRO | 60 | 500,000 |
| ENTERPRISE | Unlimited | Unlimited |

Token estimate: 1 token ≈ 4 characters (prompt + completion).

## Endpoints

Users are identified by the `X-User-Id` header (defaults to `demo`).

| Method | Path | Description |
|---|---|---|
| POST | `/api/ai/generate` | Body `{"prompt": "..."}`. Goes through the whole proxy chain |
| GET | `/api/quota/status` | Used tokens, remaining tokens, reset date, plan |
| GET | `/api/quota/history` | Daily usage for the last 7 days |
| POST | `/api/quota/upgrade` | Upgrades the plan (FREE → PRO). Optional body `{"plan":"ENTERPRISE"}` |

## Scheduled tasks (`scheduler/ResetScheduler`)

- `0 * * * * *` → resets the request counter every minute.
- `0 0 0 1 * *` → resets the monthly quota on the first day of each month.

## Run locally

```bash
mvn spring-boot:run
curl -X POST localhost:8080/api/ai/generate -H "Content-Type: application/json" \
     -H "X-User-Id: david" -d '{"prompt":"Hello AI"}'
```

## Deploy to Railway

1. New Project → Deploy from GitHub repo → `ia-proxy-backend` (uses the `Dockerfile`).
2. Variables: `ALLOWED_ORIGINS=https://<your-frontend>.vercel.app`
3. Settings → Networking → Generate Domain.

> Data is stored in memory, so it is cleared when the server restarts.
