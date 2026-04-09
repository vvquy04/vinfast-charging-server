# 07 — Danh sách tính năng (Features)

> Tài liệu theo dõi tất cả tính năng của hệ thống **VinFast EV Charging Server**.
> Cập nhật trạng thái khi hoàn thành từng phần.

---

## Ký hiệu trạng thái

| Ký hiệu | Ý nghĩa                  |
|----------|---------------------------|
| ⬜       | Chưa bắt đầu              |
| 🔨       | Đang phát triển            |
| ✅       | Hoàn thành                 |
| ❌       | Hủy / không triển khai     |

---

## 1. Authentication & User Management

| #   | Tính năng                        | Trạng thái | Ghi chú                                 |
|-----|----------------------------------|-----------|------------------------------------------|
| 1.1 | Đăng ký tài khoản               | ⬜        | `POST /api/auth/register`                |
| 1.2 | Đăng nhập                       | ⬜        | `POST /api/auth/login`, trả JWT token    |
| 1.3 | Xem thông tin cá nhân            | ⬜        | `GET /api/users/me`                      |
| 1.4 | Cập nhật thông tin cá nhân       | ⬜        | `PUT /api/users/me`                      |
| 1.5 | Đổi mật khẩu                    | ⬜        | `PUT /api/users/me/password`             |
| 1.6 | JWT token generation & validation| ⬜        | Spring Security + JWT filter             |

---

## 2. Charging Stations

| #   | Tính năng                                  | Trạng thái | Ghi chú                                      |
|-----|--------------------------------------------|-----------|------------------------------------------------|
| 2.1 | Lấy danh sách trạm sạc gần vị trí          | ⬜        | `GET /api/stations` + Haversine formula        |
| 2.2 | Lọc trạm theo loại cổng sạc               | ⬜        | Query param `connectorType`                    |
| 2.3 | Xem chi tiết trạm sạc                     | ⬜        | `GET /api/stations/{id}` + connectors + reviews|
| 2.4 | Tính khoảng cách đến trạm                 | ⬜        | Haversine util class                           |
| 2.5 | Phân trang kết quả tìm kiếm               | ⬜        | Spring Data Pageable                           |

---

## 3. Reviews & Ratings

| #   | Tính năng                                  | Trạng thái | Ghi chú                                      |
|-----|--------------------------------------------|-----------|------------------------------------------------|
| 3.1 | Xem danh sách đánh giá của trạm            | ⬜        | `GET /api/stations/{id}/reviews` + phân trang  |
| 3.2 | Thêm đánh giá (1-5 sao + comment)          | ⬜        | `POST /api/stations/{id}/reviews`              |
| 3.3 | Xóa đánh giá (chỉ chủ sở hữu)             | ⬜        | `DELETE /api/stations/{id}/reviews/{reviewId}` |
| 3.4 | Tự động cập nhật rating trung bình của trạm | ⬜        | Cập nhật `rating` + `total_reviews` trong service |

---

## 4. User History

| #   | Tính năng                           | Trạng thái | Ghi chú                                    |
|-----|--------------------------------------|-----------|----------------------------------------------|
| 4.1 | Ghi nhận lượt xem trạm              | ⬜        | `POST /api/users/me/history`                 |
| 4.2 | Xem lịch sử trạm đã truy cập       | ⬜        | `GET /api/users/me/history` + phân trang     |
| 4.3 | Đếm số lần truy cập (visit_count)   | ⬜        | Auto-increment khi xem lại cùng trạm        |

---

## 5. Infrastructure & Config

| #   | Tính năng                            | Trạng thái | Ghi chú                                       |
|-----|--------------------------------------|-----------|------------------------------------------------|
| 5.1 | Cấu trúc project Spring Boot        | ✅        | Package structure đã tạo                       |
| 5.2 | Kết nối MySQL database              | ✅        | `application.properties` đã cấu hình           |
| 5.3 | Spring Security config              | ⬜        | CORS, JWT filter, SecurityFilterChain           |
| 5.4 | Global Exception Handler            | ⬜        | `@RestControllerAdvice` + `ApiResponse`         |
| 5.5 | API Response wrapper (ApiResponse)   | ⬜        | Format response nhất quán                      |
| 5.6 | Database schema / migration          | ✅        | SQL script sẵn sàng, `ddl-auto=update`         |

---

## 6. Thứ tự triển khai đề xuất

> Thứ tự đề xuất dựa trên dependency giữa các tính năng.

### Phase 1 — Foundation (ưu tiên cao nhất)
1. **5.4** Global Exception Handler + **5.5** ApiResponse wrapper
2. **1.1** Đăng ký + **1.2** Đăng nhập + **1.6** JWT
3. **5.3** Spring Security config

### Phase 2 — Core Features
4. **2.1** Tìm trạm gần vị trí + **2.4** Haversine + **2.5** Phân trang
5. **2.3** Chi tiết trạm + **2.2** Lọc theo connector
6. **1.3** Xem profile + **1.4** Cập nhật profile + **1.5** Đổi mật khẩu

### Phase 3 — Social & History
7. **3.1** Xem đánh giá + **3.2** Thêm đánh giá + **3.4** Auto-update rating
8. **3.3** Xóa đánh giá
9. **4.1** Ghi nhận lượt xem + **4.2** Xem lịch sử + **4.3** Visit count

---

## 7. Tổng kết tiến độ

| Module                | Tổng | ✅ Done | ⬜ Todo | Tiến độ |
|-----------------------|------|---------|---------|---------|
| Authentication & User | 6    | 0       | 6       | 0%      |
| Charging Stations     | 5    | 0       | 5       | 0%      |
| Reviews & Ratings     | 4    | 0       | 4       | 0%      |
| User History          | 3    | 0       | 3       | 0%      |
| Infrastructure        | 6    | 2       | 4       | 33%     |
| **Tổng cộng**         | **24** | **2** | **22**  | **8%** |

> 📅 Cập nhật lần cuối: 2026-04-08
