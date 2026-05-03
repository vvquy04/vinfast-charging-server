# Plan: EVCPoint MVP — Server (Spring Boot)

> Kế hoạch triển khai kỹ thuật chi tiết cho backend REST API.

---

## 1. Thứ tự triển khai

Dựa trên dependency giữa các module, thứ tự triển khai được đề xuất:

```
Phase 1: Foundation
  ├── 1.1 Global Exception Handler + ApiResponse wrapper
  ├── 1.2 JPA Entity classes (5 bảng)
  └── 1.3 Spring Security + JWT (filter, generation, validation)

Phase 2: Auth Module
  ├── 2.1 AuthController + AuthService (register, login)
  └── 2.2 UserController + UserService (profile CRUD, change password)

Phase 3: Station Module
  ├── 3.1 StationController + StationService (list, detail, nearby)
  ├── 3.2 Haversine util class
  └── 3.3 ConnectorType sub-resource trong Station

Phase 4: Social Module
  ├── 4.1 ReviewController + ReviewService (CRUD + auto-update rating)
  └── 4.2 HistoryController + HistoryService (record + list)

Phase 5: Data Seeding
  └── 5.1 DataSeeder (CommandLineRunner) — dữ liệu mẫu
```

---

## 2. Chi tiết triển khai theo file

### Phase 1 — Foundation

#### `exception/GlobalExceptionHandler.java`
- `@RestControllerAdvice`
- Handle: `ResourceNotFoundException`, `DuplicateResourceException`, `BadRequestException`, `AccessDeniedException`
- Trả response format chuẩn `ApiResponse`

#### `dto/response/ApiResponse.java`
- Generic class: `ApiResponse<T>` với `success`, `message`, `data`
- Static factory: `ApiResponse.success(data, message)`, `ApiResponse.error(message)`

#### `model/*.java` — 5 JPA Entities
- `User.java` → `@Table(name = "users")`
- `ChargingStation.java` → `@Table(name = "charging_stations")`
- `ConnectorType.java` → `@Table(name = "connector_types")`
- `Review.java` → `@Table(name = "reviews")`
- `UserStationHistory.java` → `@Table(name = "user_station_history")`
- Tất cả dùng Lombok: `@Data`, `@NoArgsConstructor`, `@AllArgsConstructor`, `@Builder`
- Quan hệ: `@ManyToOne(fetch = LAZY)`, `@OneToMany(mappedBy, cascade)`

#### `config/SecurityConfig.java`
- `SecurityFilterChain` bean
- Permit all: `/api/auth/**`, `GET /api/stations/**`
- Require auth: tất cả endpoint còn lại
- Add `JwtAuthenticationFilter` before `UsernamePasswordAuthenticationFilter`
- CORS config: allow `*`

#### `util/JwtUtil.java`
- Generate token từ `userId` + `phoneNumber`
- Validate token, extract claims
- Secret key + expiration (24h) trong `application.properties`

#### `config/JwtAuthenticationFilter.java`
- Extends `OncePerRequestFilter`
- Extract token từ `Authorization` header
- Validate → set `SecurityContext`

### Phase 2 — Auth Module

#### `dto/request/RegisterRequest.java`
- Fields: `fullName`, `phoneNumber`, `password`, `email`, `vehicleModel`, `connectorType`

#### `dto/request/LoginRequest.java`
- Fields: `phoneNumber`, `password`

#### `dto/response/AuthResponse.java`
- Fields: `userId`, `fullName`, `phoneNumber`, `token`

#### `dto/response/UserResponse.java`
- Full user info (không có `passwordHash`)

#### `repository/UserRepository.java`
- `findByPhoneNumber(String phoneNumber)`
- `existsByPhoneNumber(String phoneNumber)`
- `existsByEmail(String email)`

#### `service/AuthService.java` + `service/impl/AuthServiceImpl.java`
- `register(RegisterRequest)` → hash password, save, generate JWT
- `login(LoginRequest)` → validate credentials, generate JWT

#### `controller/AuthController.java`
- `POST /api/auth/register`
- `POST /api/auth/login`

#### `service/UserService.java` + `service/impl/UserServiceImpl.java`
- `getProfile(Long userId)` → return UserResponse
- `updateProfile(Long userId, UpdateProfileRequest)`
- `changePassword(Long userId, ChangePasswordRequest)`

#### `controller/UserController.java`
- `GET /api/users/me`
- `PUT /api/users/me`
- `PUT /api/users/me/password`

### Phase 3 — Station Module

#### `util/HaversineUtil.java`
- `static double calculateDistance(lat1, lon1, lat2, lon2)` → km

#### `dto/response/StationSummaryResponse.java`
- Station basic info + `distance` field

#### `dto/response/StationDetailResponse.java`
- Station full info + `connectorTypes[]` + `recentReviews[]`

#### `dto/response/ConnectorTypeResponse.java`
- `type`, `powerKw`, `totalPorts`

#### `repository/StationRepository.java`
- `findByIsActiveTrue(Pageable pageable)`
- Custom query: find stations within radius using Haversine

#### `repository/ConnectorTypeRepository.java`
- `findByStationStationId(Long stationId)`

#### `service/StationService.java` + `service/impl/StationServiceImpl.java`
- `searchStations(lat, lng, radius, connectorType, pageable)` → tính distance, sort, filter
- `getStationDetail(stationId)` → station + connectors + recent reviews

#### `controller/StationController.java`
- `GET /api/stations`
- `GET /api/stations/{stationId}`

### Phase 4 — Social Module

#### `dto/request/ReviewRequest.java`
- `rating` (1–5), `comment`

#### `dto/response/ReviewResponse.java`
- `reviewId`, `userId`, `userName`, `rating`, `comment`, `createdAt`

#### `repository/ReviewRepository.java`
- `findByStationStationId(Long stationId, Pageable pageable)`
- `findTop5ByStationStationIdOrderByCreatedAtDesc(Long stationId)`

#### `service/ReviewService.java` + `service/impl/ReviewServiceImpl.java`
- `getReviews(stationId, pageable)`
- `createReview(userId, stationId, ReviewRequest)` → save + update station rating
- `deleteReview(userId, reviewId)` → check ownership + delete + update station rating
- `recalculateStationRating(stationId)` → AVG + COUNT

#### `controller/ReviewController.java`
- `GET /api/stations/{stationId}/reviews`
- `POST /api/stations/{stationId}/reviews`
- `DELETE /api/stations/{stationId}/reviews/{reviewId}`

#### `dto/request/HistoryRequest.java`
- `stationId`

#### `dto/response/HistoryResponse.java`
- `stationId`, `stationName`, `visitCount`, `lastVisited`

#### `repository/UserStationHistoryRepository.java`
- `findByUserUserIdOrderByLastVisitedDesc(Long userId, Pageable pageable)`
- `findByUserUserIdAndStationStationId(Long userId, Long stationId)`

#### `service/HistoryService.java` + `service/impl/HistoryServiceImpl.java`
- `getHistory(userId, pageable)`
- `recordVisit(userId, stationId)` → upsert logic

#### `controller/HistoryController.java`
- `GET /api/users/me/history`
- `POST /api/users/me/history`

### Phase 5 — Data Seeding

#### `config/DataSeeder.java`
- `CommandLineRunner` bean
- Seed: 5 users, 10 stations (Hà Nội + TP.HCM), connector types, sample reviews

---

## 3. Dependencies (pom.xml)

```xml
spring-boot-starter-web
spring-boot-starter-data-jpa
spring-boot-starter-security
mysql-connector-j
lombok
jjwt-api + jjwt-impl + jjwt-jackson  (io.jsonwebtoken)
```

---

## 4. Configuration (application.properties)

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/vinfast_charging
spring.datasource.username=root
spring.datasource.password=...
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
jwt.secret=...
jwt.expiration=86400000
```

---

## 5. Verification Plan

### Build
```bash
mvn clean compile
```

### Run
```bash
mvn spring-boot:run
```

### Test endpoints (curl / Postman)
1. Register → Login → Lấy token
2. GET /api/stations (không cần token)
3. GET /api/stations/{id}
4. POST review (cần token)
5. GET + POST history (cần token)
