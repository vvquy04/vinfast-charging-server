# 02 — Thiết kế Database

> Tài liệu mô tả cấu trúc database **vinfast_charging** — MySQL 8, charset `utf8mb4`.

---

## 1. Thông tin kết nối

| Thuộc tính       | Giá trị                                       |
|------------------|-----------------------------------------------|
| **DBMS**         | MySQL 8                                       |
| **Database**     | `vinfast_charging`                            |
| **Charset**      | `utf8mb4` / `utf8mb4_unicode_ci`              |
| **Engine**       | InnoDB                                        |
| **JDBC URL**     | `jdbc:mysql://localhost:3306/vinfast_charging` |
| **ORM**          | Hibernate (Spring Data JPA, `ddl-auto=update`)|

---

## 2. Sơ đồ quan hệ (ERD)

```
┌──────────────┐       ┌────────────────────┐       ┌─────────────────┐
│    users     │       │ charging_stations  │       │ connector_types │
├──────────────┤       ├────────────────────┤       ├─────────────────┤
│ PK user_id   │       │ PK station_id      │◄──────│ FK station_id   │
│    full_name │       │    name            │       │ PK connector_id │
│    email     │       │    address         │       │    type (ENUM)  │
│    password  │       │    latitude        │       │    power_kw     │
│    phone     │       │    longitude       │       │    total_ports  │
│    gender    │       │    opening_hours   │       └─────────────────┘
│    dob       │       │    image_url       │
│    avatar_url│       │    rating          │
│    vehicle   │       │    total_reviews   │
│    connector │       │    is_active       │
│    created_at│       │    created_at      │
│    is_active │       └────────┬───────────┘
└──────┬───────┘                │
       │                        │
       │    ┌───────────────┐   │
       │    │   reviews     │   │
       │    ├───────────────┤   │
       ├───▶│ FK user_id    │   │
       │    │ FK station_id │◄──┤
       │    │ PK review_id  │   │
       │    │    rating     │   │
       │    │    comment    │   │
       │    │    created_at │   │
       │    └───────────────┘   │
       │                        │
       │    ┌────────────────────────┐
       │    │ user_station_history   │
       │    ├────────────────────────┤
       └───▶│ FK user_id             │
            │ FK station_id          │◄──┘
            │ PK history_id          │
            │    visit_count         │
            │    last_visited        │
            │ UQ (user_id,station_id)│
            └────────────────────────┘
```

---

## 3. Chi tiết từng bảng

### 3.1. `users` — Người dùng

| Cột             | Kiểu dữ liệu    | Ràng buộc                | Mô tả                                   |
|-----------------|------------------|--------------------------|------------------------------------------|
| `user_id`       | `BIGINT`         | PK, AUTO_INCREMENT       | ID người dùng                            |
| `full_name`     | `VARCHAR(100)`   | NOT NULL                 | Họ tên đầy đủ                            |
| `email`         | `VARCHAR(100)`   | UNIQUE                   | Email cá nhân (tùy chọn)                 |
| `password_hash` | `VARCHAR(255)`   | NOT NULL                 | Mật khẩu đã hash (BCrypt)               |
| `phone_number`  | `VARCHAR(15)`    | NOT NULL, UNIQUE         | Số điện thoại (dùng đăng nhập)           |
| `gender`        | `VARCHAR(10)`    |                          | Giới tính (MALE, FEMALE, OTHER)         |
| `date_of_birth` | `DATE`           |                          | Ngày sinh                                |
| `avatar_url`    | `TEXT`           |                          | Link ảnh đại diện                        |
| `vehicle_model` | `VARCHAR(50)`    |                          | Dòng xe VinFast (VF5, VF8, VFe34, …)    |
| `connector_type`| `VARCHAR(20)`    |                          | Loại cổng sạc ưa thích: `CCS2`, `AC`, … |
| `created_at`    | `DATETIME`       | NOT NULL, DEFAULT NOW    | Thời gian tạo tài khoản                 |
| `is_active`     | `BOOLEAN`        | NOT NULL, DEFAULT TRUE   | Trạng thái hoạt động                     |

### 3.2. `charging_stations` — Trạm sạc

| Cột             | Kiểu dữ liệu    | Ràng buộc                | Mô tả                                   |
|-----------------|------------------|--------------------------|------------------------------------------|
| `station_id`    | `BIGINT`         | PK, AUTO_INCREMENT       | ID trạm sạc                              |
| `name`          | `VARCHAR(200)`   | NOT NULL                 | Tên trạm                                 |
| `address`       | `TEXT`           | NOT NULL                 | Địa chỉ đầy đủ                           |
| `latitude`      | `DECIMAL(10,8)`  | NOT NULL                 | Vĩ độ                                    |
| `longitude`     | `DECIMAL(11,8)`  | NOT NULL                 | Kinh độ                                  |
| `opening_hours` | `VARCHAR(100)`   | DEFAULT `'24/7'`         | Giờ mở cửa                               |
| `image_url`     | `TEXT`           |                          | URL hình ảnh trạm                         |
| `rating`        | `DECIMAL(2,1)`   | DEFAULT `0.0`            | Điểm đánh giá trung bình (1.0 – 5.0)    |
| `total_reviews` | `INT`            | DEFAULT `0`              | Tổng số lượt đánh giá                    |
| `is_active`     | `BOOLEAN`        | NOT NULL, DEFAULT TRUE   | Trạm có đang hoạt động không              |
| `created_at`    | `DATETIME`       | NOT NULL, DEFAULT NOW    | Thời gian thêm trạm vào hệ thống         |

**Index:** `idx_location (latitude, longitude)` — tăng tốc tìm kiếm theo tọa độ.

### 3.3. `connector_types` — Loại cổng sạc tại trạm

| Cột             | Kiểu dữ liệu                              | Ràng buộc          | Mô tả                     |
|-----------------|--------------------------------------------|--------------------|----------------------------|
| `connector_id`  | `BIGINT`                                   | PK, AUTO_INCREMENT | ID cổng sạc                |
| `station_id`    | `BIGINT`                                   | FK → `charging_stations` | Trạm sở hữu          |
| `type`          | `ENUM('AC','DC','CCS2','CHAdeMO','Type2')` | NOT NULL           | Loại cổng                  |
| `power_kw`      | `INT`                                      | NOT NULL           | Công suất (kW)             |
| `total_ports`   | `INT`                                      | NOT NULL, DEFAULT 1| Số cổng cùng loại          |

**FK:** `station_id → charging_stations(station_id) ON DELETE CASCADE`

### 3.4. `reviews` — Đánh giá

| Cột             | Kiểu dữ liệu | Ràng buộc                        | Mô tả                          |
|-----------------|---------------|----------------------------------|---------------------------------|
| `review_id`     | `BIGINT`      | PK, AUTO_INCREMENT               | ID đánh giá                     |
| `user_id`       | `BIGINT`      | FK → `users`                     | Người đánh giá                  |
| `station_id`    | `BIGINT`      | FK → `charging_stations`         | Trạm được đánh giá              |
| `rating`        | `TINYINT`     | NOT NULL, CHECK (1–5)            | Điểm đánh giá (1 đến 5 sao)    |
| `comment`       | `TEXT`         |                                  | Nội dung nhận xét (tùy chọn)    |
| `created_at`    | `DATETIME`    | NOT NULL, DEFAULT NOW            | Thời gian đánh giá              |

> **Lưu ý:** Bảng `reviews` **không** có ràng buộc UNIQUE `(user_id, station_id)` — một người dùng có thể đánh giá cùng một trạm nhiều lần.

**FK:**
- `user_id → users(user_id) ON DELETE CASCADE`
- `station_id → charging_stations(station_id) ON DELETE CASCADE`

### 3.5. `user_station_history` — Lịch sử tương tác

| Cột             | Kiểu dữ liệu | Ràng buộc                                    | Mô tả                        |
|-----------------|---------------|----------------------------------------------|-------------------------------|
| `history_id`    | `BIGINT`      | PK, AUTO_INCREMENT                           | ID bản ghi                    |
| `user_id`       | `BIGINT`      | FK → `users`                                 | Người dùng                    |
| `station_id`    | `BIGINT`      | FK → `charging_stations`                     | Trạm đã tương tác             |
| `visit_count`   | `INT`         | NOT NULL, DEFAULT 1                          | Số lần truy cập / xem         |
| `last_visited`  | `DATETIME`    | NOT NULL, DEFAULT NOW                        | Lần truy cập gần nhất         |

**Ràng buộc:** `UNIQUE (user_id, station_id)` — mỗi cặp user-station chỉ tồn tại 1 bản ghi, cập nhật `visit_count` khi truy cập lại.

**FK:**
- `user_id → users(user_id) ON DELETE CASCADE`
- `station_id → charging_stations(station_id) ON DELETE CASCADE`

---

## 4. SQL khởi tạo

```sql
-- =====================================================
-- DATABASE: VINFAST CHARGING STATION FINDER
-- =====================================================

CREATE DATABASE IF NOT EXISTS vinfast_charging
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE vinfast_charging;

-- 1. Users
CREATE TABLE users (
    user_id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name     VARCHAR(100)  NOT NULL,
    email         VARCHAR(100)  UNIQUE,
    password_hash VARCHAR(255)  NOT NULL,
    phone_number  VARCHAR(15)   NOT NULL UNIQUE,
    gender        VARCHAR(10),
    date_of_birth DATE,
    avatar_url    TEXT,
    vehicle_model VARCHAR(50),
    connector_type VARCHAR(20),
    created_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_active     BOOLEAN       NOT NULL DEFAULT TRUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. Charging Stations
CREATE TABLE charging_stations (
    station_id    BIGINT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(200)   NOT NULL,
    address       TEXT           NOT NULL,
    latitude      DECIMAL(10,8)  NOT NULL,
    longitude     DECIMAL(11,8)  NOT NULL,
    opening_hours VARCHAR(100)   DEFAULT '24/7',
    image_url     TEXT,
    rating        DECIMAL(2,1)   DEFAULT 0.0,
    total_reviews INT            DEFAULT 0,
    is_active     BOOLEAN        NOT NULL DEFAULT TRUE,
    created_at    DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_location (latitude, longitude)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. Connector Types
CREATE TABLE connector_types (
    connector_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    station_id   BIGINT      NOT NULL,
    type         ENUM('AC','DC','CCS2','CHAdeMO','Type2') NOT NULL,
    power_kw     INT         NOT NULL,
    total_ports  INT         NOT NULL DEFAULT 1,
    FOREIGN KEY (station_id)
        REFERENCES charging_stations(station_id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. Reviews
CREATE TABLE reviews (
    review_id  BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id    BIGINT    NOT NULL,
    station_id BIGINT    NOT NULL,
    rating     TINYINT   NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comment    TEXT,
    created_at DATETIME  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id)    REFERENCES users(user_id)             ON DELETE CASCADE,
    FOREIGN KEY (station_id) REFERENCES charging_stations(station_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 5. User Station History
CREATE TABLE user_station_history (
    history_id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id      BIGINT   NOT NULL,
    station_id   BIGINT   NOT NULL,
    visit_count  INT      NOT NULL DEFAULT 1,
    last_visited DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id)    REFERENCES users(user_id)             ON DELETE CASCADE,
    FOREIGN KEY (station_id) REFERENCES charging_stations(station_id) ON DELETE CASCADE,
    UNIQUE KEY uq_user_station_hist (user_id, station_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

---

## 5. JPA Entity mapping (tham khảo)

Mỗi bảng sẽ có một Entity class tương ứng trong `model/`:

| Bảng                     | Entity class          | File                          |
|--------------------------|-----------------------|-------------------------------|
| `users`                  | `User`                | `model/User.java`            |
| `charging_stations`      | `ChargingStation`     | `model/ChargingStation.java`  |
| `connector_types`        | `ConnectorType`       | `model/ConnectorType.java`    |
| `reviews`                | `Review`              | `model/Review.java`           |
| `user_station_history`   | `UserStationHistory`  | `model/UserStationHistory.java`|

### Quy ước JPA Entity
- Annotation: `@Entity`, `@Table(name = "...")`, `@Id`, `@GeneratedValue(strategy = IDENTITY)`
- Quan hệ: `@ManyToOne` (fetch = `LAZY`), `@OneToMany` (mappedBy, cascade)
- Dùng Lombok: `@Data`, `@NoArgsConstructor`, `@AllArgsConstructor`, `@Builder`
- Tên cột: dùng `@Column(name = "snake_case")` khớp với DB

---

## 6. Ghi chú quan trọng

1. **`ddl-auto=update`** — Hibernate tự cập nhật schema khi chạy. Phù hợp giai đoạn phát triển, **không dùng cho production**.
2. **Index `idx_location`** — Index cơ bản trên `(latitude, longitude)`. Nếu cần tối ưu hơn, xem xét MySQL Spatial Index.
3. **ON DELETE CASCADE** — Xóa user/station sẽ cascade xóa reviews và history liên quan. Cân nhắc soft-delete bằng `is_active` thay vì xóa thật.
4. **Rating trung bình** — Cột `rating` trong `charging_stations` là denormalized field, cần cập nhật lại mỗi khi có review mới (trong service layer).
