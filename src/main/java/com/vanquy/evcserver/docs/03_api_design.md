# 03 — Thiết kế API (API Design)

> Tài liệu mô tả toàn bộ REST API endpoints cho hệ thống **VinFast EV Charging Station Finder**.

---

## 1. Quy ước chung

| Quy ước             | Chi tiết                                                   |
|----------------------|------------------------------------------------------------|
| **Base URL**         | `http://localhost:8080/api`                                |
| **Content-Type**     | `application/json`                                         |
| **Naming**           | URL dùng `kebab-case`, path params dùng `camelCase`        |
| **Versioning**       | Chưa áp dụng (v1 ngầm định)                               |
| **Auth**             | Bearer Token (JWT) trong header `Authorization`            |
| **HTTP Methods**     | `GET` = đọc, `POST` = tạo, `PUT` = cập nhật, `DELETE` = xóa |

### Response format chuẩn

**Thành công:**
```json
{
  "success": true,
  "message": "Mô tả ngắn",
  "data": { ... }
}
```

**Lỗi:**
```json
{
  "success": false,
  "message": "Mô tả lỗi",
  "data": null
}
```

### HTTP Status Codes sử dụng

| Code  | Ý nghĩa                                    |
|-------|---------------------------------------------|
| `200` | OK — request thành công                     |
| `201` | Created — tạo resource mới thành công       |
| `400` | Bad Request — dữ liệu đầu vào không hợp lệ |
| `401` | Unauthorized — chưa đăng nhập / token hết hạn|
| `403` | Forbidden — không có quyền truy cập         |
| `404` | Not Found — resource không tồn tại          |
| `409` | Conflict — dữ liệu bị trùng (email, …)     |
| `500` | Internal Server Error — lỗi server          |

---

## 2. Nhóm API: Authentication (`/api/auth`)

### 2.1. Đăng ký — `POST /api/auth/register`

**Mô tả:** Tạo tài khoản người dùng mới.

**Request Body:**
```json
{
  "fullName": "Nguyễn Văn A",
  "phoneNumber": "0909123456",
  "password": "matkhau123",
  "email": "nguyenvana@gmail.com",
  "vehicleModel": "VF8",
  "connectorType": "CCS2"
}
```

**Response `201 Created`:**
```json
{
  "success": true,
  "message": "Đăng ký thành công",
  "data": {
    "userId": 1,
    "fullName": "Nguyễn Văn A",
    "phoneNumber": "0909123456",
    "token": "eyJhbGciOiJIUzI1NiIs..."
  }
}
```

**Lỗi có thể gặp:**
| Code  | Trường hợp                  |
|-------|-----------------------------|
| `400` | Thiếu field bắt buộc        |
| `409` | Email đã tồn tại            |

---

### 2.2. Đăng nhập — `POST /api/auth/login`

**Request Body:**
```json
{
  "phoneNumber": "0909123456",
  "password": "matkhau123"
}
```

**Response `200 OK`:**
```json
{
  "success": true,
  "message": "Đăng nhập thành công",
  "data": {
    "userId": 1,
    "fullName": "Nguyễn Văn A",
    "phoneNumber": "0909123456",
    "token": "eyJhbGciOiJIUzI1NiIs..."
  }
}
```

**Lỗi:**
| Code  | Trường hợp                  |
|-------|-----------------------------|
| `401` | Email hoặc mật khẩu sai     |

---

## 3. Nhóm API: User Profile (`/api/users`)

> 🔒 Yêu cầu Authentication (Bearer Token)

### 3.1. Lấy thông tin cá nhân — `GET /api/users/me`

**Response `200`:**
```json
{
  "success": true,
  "message": "OK",
  "data": {
    "userId": 1,
    "fullName": "Nguyễn Văn A",
    "phoneNumber": "0909123456",
    "email": "nguyenvana@gmail.com",
    "gender": "MALE",
    "dateOfBirth": "1995-10-20",
    "avatarUrl": "https://example.com/avatar.jpg",
    "vehicleModel": "VF8",
    "connectorType": "CCS2",
    "createdAt": "2026-04-01T10:00:00"
  }
}
```

### 3.2. Cập nhật thông tin — `PUT /api/users/me`

**Request Body:** (chỉ gửi các field muốn đổi)
```json
{
  "fullName": "Nguyễn Văn B",
  "phoneNumber": "0909999999",
  "vehicleModel": "VF9",
  "connectorType": "Type2"
}
```

**Response `200`:** Trả về thông tin đã cập nhật (cùng format với `GET /api/users/me`).

### 3.3. Đổi mật khẩu — `PUT /api/users/me/password`

**Request Body:**
```json
{
  "currentPassword": "matkhau123",
  "newPassword": "matkhaumoi456"
}
```

**Response `200`:**
```json
{
  "success": true,
  "message": "Đổi mật khẩu thành công",
  "data": null
}
```

---

## 4. Nhóm API: Charging Stations (`/api/stations`)

### 4.1. Tìm trạm sạc gần vị trí — `GET /api/stations`

**Query Parameters:**

| Param         | Kiểu     | Bắt buộc | Mô tả                             |
|---------------|----------|-----------|------------------------------------|
| `latitude`    | `double` | ✅        | Vĩ độ hiện tại của user            |
| `longitude`   | `double` | ✅        | Kinh độ hiện tại của user           |
| `radius`      | `double` | ❌        | Bán kính tìm kiếm (km). Default: 10|
| `connectorType`| `string`| ❌        | Lọc theo loại cổng: `CCS2`, `AC`…  |
| `page`        | `int`    | ❌        | Trang (bắt đầu từ 0). Default: 0   |
| `size`        | `int`    | ❌        | Số kết quả/trang. Default: 20       |

**Response `200`:**
```json
{
  "success": true,
  "message": "OK",
  "data": {
    "content": [
      {
        "stationId": 1,
        "name": "VinFast Charging - Vinhomes Grand Park",
        "address": "Đường Nguyễn Xiển, TP. Thủ Đức, TP.HCM",
        "latitude": 10.840235,
        "longitude": 106.843820,
        "openingHours": "24/7",
        "imageUrl": "https://example.com/station1.jpg",
        "rating": 4.5,
        "totalReviews": 12,
        "distance": 2.3,
        "connectorTypes": [
          { "type": "CCS2", "powerKw": 60, "totalPorts": 4 },
          { "type": "AC", "powerKw": 22, "totalPorts": 2 }
        ]
      }
    ],
    "totalElements": 45,
    "totalPages": 3,
    "currentPage": 0
  }
}
```

### 4.2. Chi tiết trạm — `GET /api/stations/{stationId}`

**Response `200`:**
```json
{
  "success": true,
  "message": "OK",
  "data": {
    "stationId": 1,
    "name": "VinFast Charging - Vinhomes Grand Park",
    "address": "Đường Nguyễn Xiển, TP. Thủ Đức, TP.HCM",
    "latitude": 10.840235,
    "longitude": 106.843820,
    "openingHours": "24/7",
    "imageUrl": "https://example.com/station1.jpg",
    "rating": 4.5,
    "totalReviews": 12,
    "connectorTypes": [
      { "type": "CCS2", "powerKw": 60, "totalPorts": 4 },
      { "type": "AC", "powerKw": 22, "totalPorts": 2 }
    ],
    "recentReviews": [
      {
        "reviewId": 10,
        "userName": "Trần Văn B",
        "rating": 5,
        "comment": "Trạm sạc nhanh, sạch sẽ",
        "createdAt": "2026-03-15T14:30:00"
      }
    ]
  }
}
```

**Lỗi:**
| Code  | Trường hợp            |
|-------|-----------------------|
| `404` | Station không tồn tại |

---

## 5. Nhóm API: Reviews (`/api/stations/{stationId}/reviews`)

> 🔒 POST / PUT / DELETE yêu cầu Authentication

### 5.1. Lấy danh sách đánh giá — `GET /api/stations/{stationId}/reviews`

**Query Parameters:**

| Param  | Kiểu  | Bắt buộc | Mô tả                       |
|--------|-------|-----------|------------------------------|
| `page` | `int` | ❌        | Trang. Default: 0            |
| `size` | `int` | ❌        | Số kết quả/trang. Default: 10|

**Response `200`:**
```json
{
  "success": true,
  "message": "OK",
  "data": {
    "content": [
      {
        "reviewId": 10,
        "userId": 2,
        "userName": "Trần Văn B",
        "rating": 5,
        "comment": "Trạm sạc nhanh, sạch sẽ",
        "createdAt": "2026-03-15T14:30:00"
      }
    ],
    "totalElements": 12,
    "totalPages": 2,
    "currentPage": 0
  }
}
```

### 5.2. Thêm đánh giá — `POST /api/stations/{stationId}/reviews`

**Request Body:**
```json
{
  "rating": 4,
  "comment": "Trạm ổn, hơi đông vào giờ cao điểm"
}
```

**Response `201` Created:**
```json
{
  "success": true,
  "message": "Đánh giá thành công",
  "data": {
    "reviewId": 15,
    "rating": 4,
    "comment": "Trạm ổn, hơi đông vào giờ cao điểm",
    "createdAt": "2026-04-08T10:00:00"
  }
}
```

> **Lưu ý:** Sau khi tạo review, server tự động cập nhật `rating` và `total_reviews` của trạm.

### 5.3. Xóa đánh giá — `DELETE /api/stations/{stationId}/reviews/{reviewId}`

**Response `200`:**
```json
{
  "success": true,
  "message": "Đã xóa đánh giá",
  "data": null
}
```

**Lỗi:**
| Code  | Trường hợp                                 |
|-------|--------------------------------------------|
| `403` | Không phải chủ sở hữu đánh giá             |
| `404` | Review không tồn tại                        |

---

## 6. Nhóm API: User History (`/api/users/me/history`)

> 🔒 Yêu cầu Authentication

### 6.1. Lấy lịch sử trạm đã xem — `GET /api/users/me/history`

**Query Parameters:**

| Param  | Kiểu  | Bắt buộc | Mô tả                       |
|--------|-------|-----------|------------------------------|
| `page` | `int` | ❌        | Trang. Default: 0            |
| `size` | `int` | ❌        | Số kết quả/trang. Default: 20|

**Response `200`:**
```json
{
  "success": true,
  "message": "OK",
  "data": {
    "content": [
      {
        "stationId": 1,
        "stationName": "VinFast Charging - Vinhomes Grand Park",
        "visitCount": 5,
        "lastVisited": "2026-04-07T18:00:00"
      }
    ],
    "totalElements": 8,
    "totalPages": 1,
    "currentPage": 0
  }
}
```

### 6.2. Ghi nhận lượt xem — `POST /api/users/me/history`

> Tự động tạo hoặc cập nhật `visit_count` + `last_visited`.

**Request Body:**
```json
{
  "stationId": 1
}
```

**Response `200`:**
```json
{
  "success": true,
  "message": "Đã ghi nhận",
  "data": null
}
```

---

## 7. Tổng hợp Endpoints

| Method   | Endpoint                                         | Auth | Mô tả                    |
|----------|--------------------------------------------------|------|---------------------------|
| `POST`   | `/api/auth/register`                             | ❌   | Đăng ký                  |
| `POST`   | `/api/auth/login`                                | ❌   | Đăng nhập                |
| `GET`    | `/api/users/me`                                  | 🔒   | Thông tin cá nhân         |
| `PUT`    | `/api/users/me`                                  | 🔒   | Cập nhật profile          |
| `PUT`    | `/api/users/me/password`                         | 🔒   | Đổi mật khẩu             |
| `GET`    | `/api/stations`                                  | ❌   | Tìm trạm gần vị trí      |
| `GET`    | `/api/stations/{stationId}`                      | ❌   | Chi tiết trạm             |
| `GET`    | `/api/stations/{stationId}/reviews`              | ❌   | Danh sách đánh giá        |
| `POST`   | `/api/stations/{stationId}/reviews`              | 🔒   | Thêm đánh giá            |
| `DELETE` | `/api/stations/{stationId}/reviews/{reviewId}`    | 🔒   | Xóa đánh giá             |
| `GET`    | `/api/users/me/history`                          | 🔒   | Lịch sử trạm đã xem      |
| `POST`   | `/api/users/me/history`                          | 🔒   | Ghi nhận lượt xem         |

---

## 8. Ghi chú

- **Pagination:** Tất cả endpoint trả danh sách đều hỗ trợ `page` + `size` (Spring Data Pageable).
- **Distance:** Khoảng cách (`distance`) được tính bằng công thức Haversine trên server, đơn vị km.
- **Rating auto-update:** Mỗi khi tạo/xóa review, service tự động tính lại `AVG(rating)` và `COUNT(*)` cho `charging_stations`.
- **Soft delete:** Cân nhắc dùng `is_active = false` thay vì `DELETE` thực sự cho users và stations.
