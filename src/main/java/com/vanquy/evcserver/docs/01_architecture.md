# 01 — Kiến trúc hệ thống (Architecture)

> Tài liệu mô tả kiến trúc tổng thể của **VinFast EV Charging Station Finder** — backend server.

---

## 1. Tổng quan dự án

| Thông tin         | Chi tiết                                     |
|-------------------|----------------------------------------------|
| **Tên dự án**     | VinFast Charging Station Finder — Server     |
| **Mô tả**        | REST API server phục vụ ứng dụng tìm kiếm và đánh giá trạm sạc xe điện VinFast |
| **Loại dự án**    | Đồ án tốt nghiệp (DATN)                     |
| **Nền tảng**      | Spring Boot 4.0 (Java 17)                    |
| **Database**      | MySQL 8 — `vinfast_charging`                 |
| **Port mặc định** | `8080`                                       |

---

## 2. Tech Stack

| Layer            | Công nghệ                          | Ghi chú                              |
|------------------|-------------------------------------|---------------------------------------|
| **Framework**    | Spring Boot 4.0.5                   | Starter: Web MVC, Data JPA, Security |
| **Ngôn ngữ**     | Java 17                             |                                       |
| **ORM**          | Hibernate (qua Spring Data JPA)     | `ddl-auto=update`                    |
| **Database**     | MySQL 8 + `mysql-connector-j`       | Charset: `utf8mb4`                   |
| **Security**     | Spring Security                     | JWT (dự kiến)                        |
| **Build tool**   | Maven                               |                                       |
| **Boilerplate**  | Lombok                              | `@Data`, `@Builder`, ...             |

---

## 3. Kiến trúc phân lớp (Layered Architecture)

```
┌──────────────────────────────────────────────────┐
│                   Client (Mobile App)            │
└──────────────────────┬───────────────────────────┘
                       │  HTTP / REST (JSON)
                       ▼
┌──────────────────────────────────────────────────┐
│               Controller Layer                   │
│   Nhận request, validate input, trả response     │
└──────────────────────┬───────────────────────────┘
                       │
                       ▼
┌──────────────────────────────────────────────────┐
│                Service Layer                     │
│   Business logic, orchestration, transaction     │
│   ┌─────────────────────────────────────────┐    │
│   │  Service Interface  →  ServiceImpl      │    │
│   └─────────────────────────────────────────┘    │
└──────────────────────┬───────────────────────────┘
                       │
                       ▼
┌──────────────────────────────────────────────────┐
│              Repository Layer                    │
│   Spring Data JPA — truy xuất dữ liệu           │
└──────────────────────┬───────────────────────────┘
                       │
                       ▼
┌──────────────────────────────────────────────────┐
│              MySQL Database                      │
│   vinfast_charging (utf8mb4)                     │
└──────────────────────────────────────────────────┘
```

---

## 4. Cấu trúc package

```
com/vanquy/evcserver/
├── EvcserverApplication.java      # Main entry point
├── config/                         # Cấu hình (Security, CORS, …)
├── controller/                     # REST controllers — nhận/trả HTTP
├── service/                        # Business logic interface
│   └── impl/                       # Triển khai service
├── repository/                     # Spring Data JPA repositories
├── model/                          # JPA Entity classes
├── dto/                            # Data Transfer Objects
│   ├── request/                    # Request DTOs (client → server)
│   └── response/                   # Response DTOs (server → client)
├── exception/                      # Custom exceptions & global handler
├── util/                           # Utility / helper classes
└── docs/                           # Tài liệu dự án (thư mục này)
```

### Mô tả chi tiết từng package

| Package              | Vai trò                                                                 |
|----------------------|-------------------------------------------------------------------------|
| `config/`            | Cấu hình bean: SecurityConfig, CorsConfig, …                           |
| `controller/`        | Nhận HTTP request, gọi service, trả `ResponseEntity`                   |
| `service/`           | Interface định nghĩa nghiệp vụ                                         |
| `service/impl/`      | Class triển khai nghiệp vụ, inject repository, xử lý transaction       |
| `repository/`        | Interface kế thừa `JpaRepository`, chứa custom query                   |
| `model/`             | JPA entity ánh xạ bảng trong database — dùng `@Entity`, `@Table`       |
| `dto/request/`       | POJO nhận dữ liệu từ client (e.g. `RegisterRequest`, `ReviewRequest`) |
| `dto/response/`      | POJO trả dữ liệu về client (e.g. `StationResponse`, `UserResponse`)   |
| `exception/`         | Custom exception + `@ControllerAdvice` xử lý lỗi toàn cục              |
| `util/`              | Helper: tính khoảng cách Haversine, format, …                          |

---

## 5. Luồng xử lý request (Request Flow)

```
Client Request
     │
     ▼
[Controller]  ──validate──▶  Có lỗi?  ──YES──▶  Trả 400 Bad Request
     │                                                    
     │ NO                                                 
     ▼                                                    
[Service]     ──business──▶  Có lỗi?  ──YES──▶  Throw Custom Exception
     │                                    │               
     │ OK                                 ▼               
     ▼                          [@ControllerAdvice]       
[Repository]                     Trả 404/409/500          
     │                                                    
     ▼                                                    
[Database]                                                
     │                                                    
     ▼                                                    
Response DTO  ◀──mapping──  Entity                        
     │                                                    
     ▼                                                    
Client Response (JSON)                                    
```

---

## 6. Nguyên tắc thiết kế

1. **Tách biệt rõ ràng giữa các layer** — Controller không chứa logic, Service không biết HTTP.
2. **Dùng DTO thay vì trả Entity trực tiếp** — tránh lộ cấu trúc database, kiểm soát dữ liệu trả về.
3. **Service Interface + Impl** — dễ test, dễ thay thế implementation.
4. **Global Exception Handler** — xử lý lỗi nhất quán, tránh try-catch phân tán.
5. **Convention over Configuration** — tận dụng auto-config của Spring Boot.

---

## 7. Deployment (dự kiến)

| Môi trường      | Chi tiết                                       |
|-----------------|------------------------------------------------|
| **Development** | `localhost:8080`, MySQL local                  |
| **Production**  | VPS / Cloud (AWS EC2, DigitalOcean, …) — TBD  |
| **Build**       | `mvn clean package -DskipTests`                |
| **Run**         | `java -jar target/evcserver-0.0.1-SNAPSHOT.jar`|
