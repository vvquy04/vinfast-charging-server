# Tasks: EVCPoint MVP — Server (Spring Boot)

> Danh sách task triển khai, sắp xếp theo thứ tự dependency. `[P]` = có thể chạy song song.

---

## Phase 1 — Foundation

- [ ] **1.1** Tạo `dto/response/ApiResponse.java` — Generic response wrapper
  - File: `src/main/java/com/vanquy/evcserver/dto/response/ApiResponse.java`
  - Checkpoint: Class compile thành công

- [ ] **1.2** Tạo `exception/` package — Custom exceptions + Global handler
  - Files:
    - `exception/ResourceNotFoundException.java`
    - `exception/DuplicateResourceException.java`
    - `exception/BadRequestException.java`
    - `exception/GlobalExceptionHandler.java`
  - Checkpoint: Handler trả JSON format chuẩn khi throw exception

- [ ] **1.3** Tạo/cập nhật 5 JPA Entity classes trong `model/`
  - Files:
    - `model/User.java`
    - `model/ChargingStation.java`
    - `model/ConnectorType.java`
    - `model/Review.java`
    - `model/UserStationHistory.java`
  - Checkpoint: `mvn compile` thành công, Hibernate tạo bảng trong MySQL

- [ ] **1.4** Tạo `util/JwtUtil.java` — JWT generation & validation
  - File: `src/main/java/com/vanquy/evcserver/util/JwtUtil.java`
  - Dependencies: `jjwt` trong pom.xml
  - Checkpoint: Unit test generate + validate token

- [ ] **1.5** Tạo `config/JwtAuthenticationFilter.java` — JWT filter
  - File: `src/main/java/com/vanquy/evcserver/config/JwtAuthenticationFilter.java`
  - Checkpoint: Filter extract token từ header

- [ ] **1.6** Tạo `config/SecurityConfig.java` — Spring Security config
  - File: `src/main/java/com/vanquy/evcserver/config/SecurityConfig.java`
  - Checkpoint: Public endpoints accessible, protected endpoints return 401

---

## Phase 2 — Auth Module

- [ ] **2.1** Tạo Auth DTOs
  - Files:
    - `dto/request/RegisterRequest.java`
    - `dto/request/LoginRequest.java`
    - `dto/response/AuthResponse.java`
  - Checkpoint: Classes compile

- [ ] **2.2** Tạo `repository/UserRepository.java`
  - File: `src/main/java/com/vanquy/evcserver/repository/UserRepository.java`
  - Methods: `findByPhoneNumber`, `existsByPhoneNumber`, `existsByEmail`

- [ ] **2.3** Tạo `service/AuthService.java` + `service/impl/AuthServiceImpl.java`
  - Register: validate → hash password → save → generate JWT
  - Login: find user → verify password → generate JWT

- [ ] **2.4** Tạo `controller/AuthController.java`
  - `POST /api/auth/register` → 201
  - `POST /api/auth/login` → 200
  - Checkpoint: Test register + login via curl/Postman

- [ ] **2.5** Tạo User Profile DTOs
  - Files:
    - `dto/request/UpdateProfileRequest.java`
    - `dto/request/ChangePasswordRequest.java`
    - `dto/response/UserResponse.java`

- [ ] **2.6** Tạo `service/UserService.java` + `service/impl/UserServiceImpl.java`
  - getProfile, updateProfile, changePassword

- [ ] **2.7** Tạo `controller/UserController.java`
  - `GET /api/users/me`
  - `PUT /api/users/me`
  - `PUT /api/users/me/password`
  - Checkpoint: Test profile CRUD with JWT

---

## Phase 3 — Station Module

- [ ] **3.1** [P] Tạo `util/HaversineUtil.java`
  - Static method tính khoảng cách 2 toạ độ → km

- [ ] **3.2** Tạo Station DTOs
  - Files:
    - `dto/response/StationSummaryResponse.java`
    - `dto/response/StationDetailResponse.java`
    - `dto/response/ConnectorTypeResponse.java`

- [ ] **3.3** Tạo Repositories
  - `repository/StationRepository.java`
  - `repository/ConnectorTypeRepository.java`

- [ ] **3.4** Tạo `service/StationService.java` + `service/impl/StationServiceImpl.java`
  - searchStations: query all active → filter by radius (Haversine) → filter by connectorType → sort by distance → paginate
  - getStationDetail: station + connectors + top 5 reviews

- [ ] **3.5** Tạo `controller/StationController.java`
  - `GET /api/stations?latitude=...&longitude=...&radius=...&connectorType=...&page=...&size=...`
  - `GET /api/stations/{stationId}`
  - Checkpoint: Test search + detail via curl

---

## Phase 4 — Social Module

- [ ] **4.1** Tạo Review DTOs
  - `dto/request/ReviewRequest.java`
  - `dto/response/ReviewResponse.java`

- [ ] **4.2** Tạo `repository/ReviewRepository.java`
  - `findByStationStationId(stationId, pageable)`
  - `findTop5ByStation_StationIdOrderByCreatedAtDesc(stationId)`

- [ ] **4.3** Tạo `service/ReviewService.java` + `service/impl/ReviewServiceImpl.java`
  - CRUD + recalculateStationRating

- [ ] **4.4** Tạo `controller/ReviewController.java`
  - `GET /api/stations/{stationId}/reviews`
  - `POST /api/stations/{stationId}/reviews`
  - `DELETE /api/stations/{stationId}/reviews/{reviewId}`
  - Checkpoint: Test full review flow

- [ ] **4.5** Tạo History DTOs
  - `dto/request/HistoryRequest.java`
  - `dto/response/HistoryResponse.java`

- [ ] **4.6** Tạo `repository/UserStationHistoryRepository.java`

- [ ] **4.7** Tạo `service/HistoryService.java` + `service/impl/HistoryServiceImpl.java`
  - getHistory + recordVisit (upsert)

- [ ] **4.8** Tạo `controller/HistoryController.java`
  - `GET /api/users/me/history`
  - `POST /api/users/me/history`
  - Checkpoint: Test history flow

---

## Phase 5 — Data Seeding

- [ ] **5.1** Cập nhật `config/DataSeeder.java`
  - Seed 5 users (password hashed)
  - Seed 10 charging stations (Hà Nội + TP.HCM) với toạ độ thực
  - Seed connector_types cho mỗi trạm
  - Seed sample reviews
  - Checkpoint: App start → database có dữ liệu mẫu

---

## Final Verification

- [ ] `mvn clean compile` → SUCCESS
- [ ] `mvn spring-boot:run` → Server start ở port 8080
- [ ] Test full flow: register → login → search stations → view detail → create review → view history
