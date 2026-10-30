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

The frontend is deployed on Vercel from [ia-proxy-frontend](https://github.com/davidcampino06/ia-proxy-frontend).

Users are identified by the `X-User-Id` header (defaults to `demo`).

| Method | Path | Description |
|---|---|---|
| POST | `/api/ai/generate` | Body `{"prompt": "..."}`. Goes through the whole proxy chain |
| GET | `/api/quota/status` | Used tokens, remaining tokens, reset date, plan |
| GET | `/api/quota/history` | Daily usage for the last 7 days |
| POST | `/api/quota/upgrade` | Upgrades the plan (FREE → PRO). Optional body `{"plan":"ENTERPRISE"}` |
| GET | `/api/plans` | All plans with their limits and price |

Every `/api/ai/generate` response includes `steps`: the layers the request went through
(`RateLimitProxy`, `QuotaProxy`, `MockAIGenerationService`) with what each one did and how long it took.

### Demo endpoints

Shortcuts for the presentation. They change the account state but never bypass the proxies.
Turn them off with `DEMO_ENABLED=false`.

| Method | Path | Description |
|---|---|---|
| POST | `/api/demo/exhaust-quota` | Uses up the monthly quota, so the next prompt gets 402 |
| POST | `/api/demo/seed-history` | Fills the last 6 days with sample usage |
| POST | `/api/demo/reset` | Back to a new FREE account with no usage |

## Scheduled tasks (`scheduler/ResetScheduler`)

- `0 * * * * *` → resets the request counter every minute.
- `0 0 0 1 * *` → resets the monthly quota on the first day of each month.

## Run locally

```bash
mvn spring-boot:run
curl -X POST localhost:8080/api/ai/generate -H "Content-Type: application/json" \
     -H "X-User-Id: david" -d '{"prompt":"Hello AI"}'
```

## Deployment

Live API: https://ia-proxy-backend.onrender.com (health check: `/api/health`)

Deployed on **Render** (free web service, Docker runtime):

1. New → Web Service → connect the `ia-proxy-backend` repository.
2. Language: Docker (detected from the `Dockerfile`). Instance type: Free.
3. Optional environment variables: `ALLOWED_ORIGINS`, `DEMO_ENABLED`, `APP_TIMEZONE`.
4. Health check path: `/api/health`.

The free instance sleeps after 15 minutes without traffic and takes about a minute to wake up,
so open `/api/health` before a demo.

> Data is stored in memory, so it is cleared when the server restarts.
