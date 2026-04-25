# booking-service (:8083) — CORE

Service trung tâm: tạo booking, publish event, listen kết quả thanh toán.

## Endpoint

| Method | Path                | Auth   | Mô tả                                  |
| ------ | ------------------- | ------ | -------------------------------------- |
| POST   | `/bookings`         | Bearer | Tạo booking, **trả `202 Accepted`** + `status=PENDING` |
| GET    | `/bookings`         | Bearer | List booking của user hiện tại          |
| GET    | `/bookings/{id}`    | Bearer | Chi tiết 1 booking (chỉ owner xem được) |

`userId` được lấy từ JWT (claim `sub`).

### Body POST `/bookings`

```json
{
  "movieId": "m-incep01",
  "showtime": "2026-04-25T19:00:00Z",
  "seats": ["A1", "A2"],
  "unitPrice": 120000
}
```

> **Vì sao client phải gửi `unitPrice`?** Service **không** gọi cross-service
> sang `movie-service` (theo đề bài cấm). FE đã có data movie từ `GET /movies`
> nên gửi kèm `unitPrice` luôn. Server tính `amount = unitPrice * seats.size`.
> Xem [ai-agent/memory/decisions.md](../ai-agent/memory/decisions.md) ADR-007.

## Event publish & consume

| Direction | Event                | Routing key          | Queue (consume)                       |
| --------- | -------------------- | -------------------- | ------------------------------------- |
| publish   | `BOOKING_CREATED`    | `booking.created`    | —                                     |
| consume   | `PAYMENT_COMPLETED`  | `payment.completed`  | `booking-service.payment.completed`   |
| consume   | `BOOKING_FAILED`     | `booking.failed`     | `booking-service.booking.failed`      |

Idempotency: bảng `processed_events(event_id PK)` chặn xử lý trùng.

## Env

| Biến              | Default                                |
| ----------------- | -------------------------------------- |
| `DB_HOST/PORT`    | `localhost:5432`                       |
| `RABBITMQ_HOST`   | `localhost`                            |
| `JWT_SECRET`      | (giống các service khác)               |

## Run local

```bash
./mvnw spring-boot:run
```

## Test

```bash
# Unit test (không cần Docker)
./mvnw test -Dtest=BookingServiceImplTest

# Integration test (cần Docker — Testcontainers)
./mvnw test -Dtest=BookingFlowIT
```
