# Spec: EVCPoint MVP — VinFast EV Charging Server

> Đặc tả tổng hợp cho backend REST API phục vụ ứng dụng tìm kiếm và đánh giá trạm sạc xe điện VinFast.

---

## 1. Tổng quan

### Mục tiêu
Xây dựng REST API server bằng **Spring Boot 4.0 (Java 17)** + **MySQL 8**, cung cấp đầy đủ endpoint cho ứng dụng Flutter client, bao gồm:
- Xác thực người dùng (đăng ký, đăng nhập) với JWT
- Quản lý hồ sơ người dùng
- Tìm kiếm và xem chi tiết trạm sạc
- Đánh giá trạm sạc
- Lịch sử trạm đã xem

### Tech Stack
| Layer | Công nghệ |
|-------|-----------|
| Framework | Spring Boot 4.0.5 (Web MVC, Data JPA, Security) |
| Ngôn ngữ | Java 17 |
| ORM | Hibernate (Spring Data JPA, `ddl-auto=update`) |
| Database | MySQL 8, database `vinfast_charging`, charset `utf8mb4` |
| Security | Spring Security + JWT Bearer Token |
| Build | Maven |
| Boilerplate | Lombok (`@Data`, `@Builder`) |

### Kiến trúc
```
Controller → Service (Interface + Impl) → Repository → MySQL
```
- DTO pattern: `dto/request/` (client → server), `dto/response/` (server → client)
- Global Exception Handler via `@RestControllerAdvice`
- Response wrapper: `{ success, message, data }`

---

## 2. User Stories

### US-1: Đăng ký tài khoản
**Là** người dùng mới,
**Tôi muốn** tạo tài khoản bằng số điện thoại, mật khẩu, họ tên,
**Để** có thể sử dụng các tính năng cần xác thực (đánh giá, lịch sử).

**Acceptance Criteria:**
- [ ] `POST /api/auth/register` nhận `fullName`, `phoneNumber`, `password` (bắt buộc), `email`, `vehicleModel`, `connectorType` (tuỳ chọn)
- [ ] Mật khẩu được hash bằng BCrypt trước khi lưu
- [ ] Trả về `201 Created` kèm `userId`, `fullName`, `phoneNumber`, `token` (JWT)
- [ ] Trả `409 Conflict` nếu `phoneNumber` hoặc `email` đã tồn tại
- [ ] Trả `400 Bad Request` nếu thiếu field bắt buộc

### US-2: Đăng nhập
**Là** người dùng đã có tài khoản,
**Tôi muốn** đăng nhập bằng số điện thoại và mật khẩu,
**Để** nhận JWT token và truy cập các tính năng cần xác thực.

**Acceptance Criteria:**
- [ ] `POST /api/auth/login` nhận `phoneNumber`, `password`
- [ ] Xác thực mật khẩu bằng BCrypt
- [ ] Trả `200 OK` kèm `userId`, `fullName`, `phoneNumber`, `token`
- [ ] Trả `401 Unauthorized` nếu thông tin đăng nhập sai

### US-3: Xem và cập nhật hồ sơ cá nhân
**Là** người dùng đã đăng nhập,
**Tôi muốn** xem và chỉnh sửa thông tin cá nhân,
**Để** cập nhật dòng xe và loại cổng sạc phù hợp.

**Acceptance Criteria:**
- [ ] `GET /api/users/me` trả đầy đủ thông tin: `userId`, `fullName`, `phoneNumber`, `email`, `gender`, `dateOfBirth`, `avatarUrl`, `vehicleModel`, `connectorType`, `createdAt`
- [ ] `PUT /api/users/me` cho phép cập nhật các field: `fullName`, `phoneNumber`, `email`, `gender`, `dateOfBirth`, `avatarUrl`, `vehicleModel`, `connectorType`
- [ ] `PUT /api/users/me/password` cho phép đổi mật khẩu (nhận `currentPassword`, `newPassword`)
- [ ] Yêu cầu JWT token hợp lệ (401 nếu không có)

### US-4: Tìm kiếm trạm sạc gần vị trí
**Là** người dùng xe điện,
**Tôi muốn** tìm các trạm sạc gần vị trí hiện tại,
**Để** chọn trạm phù hợp nhất để đi sạc.

**Acceptance Criteria:**
- [ ] `GET /api/stations` nhận `latitude`, `longitude` (bắt buộc), `radius` (mặc định 10km), `connectorType`, `page`, `size`
- [ ] Tính khoảng cách bằng công thức **Haversine** trong util class
- [ ] Trả danh sách trạm đã sắp xếp theo khoảng cách, kèm `distance` (km)
- [ ] Hỗ trợ lọc theo `connectorType`
- [ ] Hỗ trợ phân trang (Spring Data Pageable)
- [ ] Response trả `content[]`, `totalElements`, `totalPages`, `currentPage`

### US-5: Xem chi tiết trạm sạc
**Là** người dùng,
**Tôi muốn** xem đầy đủ thông tin của một trạm sạc,
**Để** biết trạm có loại cổng sạc phù hợp và đọc đánh giá.

**Acceptance Criteria:**
- [ ] `GET /api/stations/{stationId}` trả: thông tin trạm + danh sách `connectorTypes[]` + `recentReviews[]` (5 đánh giá mới nhất)
- [ ] Trả `404 Not Found` nếu station không tồn tại

### US-6: Đánh giá trạm sạc
**Là** người dùng đã đăng nhập,
**Tôi muốn** viết đánh giá (1–5 sao + bình luận) cho một trạm,
**Để** chia sẻ trải nghiệm sạc xe của mình.

**Acceptance Criteria:**
- [ ] `GET /api/stations/{stationId}/reviews` trả danh sách đánh giá có phân trang
- [ ] `POST /api/stations/{stationId}/reviews` tạo đánh giá mới (nhận `rating`, `comment`)
- [ ] Sau khi tạo/xoá review, server tự động tính lại `AVG(rating)` và `COUNT(*)` → cập nhật `charging_stations.rating` và `charging_stations.total_reviews`
- [ ] `DELETE /api/stations/{stationId}/reviews/{reviewId}` — chỉ chủ sở hữu mới được xoá (403 nếu không phải)
- [ ] Yêu cầu JWT cho POST và DELETE

### US-7: Lịch sử trạm đã xem
**Là** người dùng đã đăng nhập,
**Tôi muốn** xem danh sách các trạm mình đã truy cập,
**Để** quay lại trạm sạc quen thuộc nhanh chóng.

**Acceptance Criteria:**
- [ ] `GET /api/users/me/history` trả lịch sử có phân trang, gồm `stationId`, `stationName`, `visitCount`, `lastVisited`
- [ ] `POST /api/users/me/history` ghi nhận lượt xem (nhận `stationId`)
- [ ] Nếu đã có bản ghi `(userId, stationId)` → tăng `visit_count` + cập nhật `last_visited`
- [ ] Nếu chưa có → tạo bản ghi mới với `visit_count = 1`
- [ ] Yêu cầu JWT

---

## 3. Database Schema

5 bảng chính (tham chiếu `02_database.md`):
- `users` — Người dùng
- `charging_stations` — Trạm sạc
- `connector_types` — Loại cổng sạc tại trạm
- `reviews` — Đánh giá
- `user_station_history` — Lịch sử tương tác

> Chi tiết cột, kiểu dữ liệu, ràng buộc: xem `docs/02_database.md`

---

## 4. API Endpoints

| Method | Endpoint | Auth | Mô tả |
|--------|----------|------|-------|
| `POST` | `/api/auth/register` | ❌ | Đăng ký |
| `POST` | `/api/auth/login` | ❌ | Đăng nhập |
| `GET` | `/api/users/me` | 🔒 | Thông tin cá nhân |
| `PUT` | `/api/users/me` | 🔒 | Cập nhật profile |
| `PUT` | `/api/users/me/password` | 🔒 | Đổi mật khẩu |
| `GET` | `/api/stations` | ❌ | Tìm trạm gần vị trí |
| `GET` | `/api/stations/{stationId}` | ❌ | Chi tiết trạm |
| `GET` | `/api/stations/{stationId}/reviews` | ❌ | Danh sách đánh giá |
| `POST` | `/api/stations/{stationId}/reviews` | 🔒 | Thêm đánh giá |
| `DELETE` | `/api/stations/{stationId}/reviews/{reviewId}` | 🔒 | Xoá đánh giá |
| `GET` | `/api/users/me/history` | 🔒 | Lịch sử trạm đã xem |
| `POST` | `/api/users/me/history` | 🔒 | Ghi nhận lượt xem |

> Chi tiết request/response format: xem `docs/03_api_design.md`

---

## 5. Constraints & Non-Functional Requirements

1. **Response format nhất quán:** `{ success: boolean, message: string, data: object|null }`
2. **Pagination:** Sử dụng Spring Data `Pageable` cho tất cả list endpoints
3. **Password hashing:** BCrypt (Spring Security)
4. **JWT:** Sử dụng `Authorization: Bearer <token>` header
5. **Soft delete:** Sử dụng `is_active` flag thay vì xoá vật lý (cho users và stations)
6. **Haversine distance:** Tính trên server, trả về đơn vị km
7. **Denormalized rating:** `charging_stations.rating` và `total_reviews` cập nhật tự động khi tạo/xoá review

---

## Review & Acceptance Checklist

- [ ] Tất cả user stories đều có acceptance criteria rõ ràng
- [ ] API endpoints khớp với `docs/03_api_design.md`
- [ ] Database schema khớp với `docs/02_database.md`
- [ ] Kiến trúc phân lớp khớp với `docs/01_architecture.md`
- [ ] Đặt tên tuân thủ `docs/04_coding_rules.md`
- [ ] Non-functional requirements được xác định
