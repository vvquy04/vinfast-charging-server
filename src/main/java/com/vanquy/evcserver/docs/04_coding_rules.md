# 04 — Quy tắc viết code (Coding Rules)

> Tài liệu quy định các chuẩn mực và quy tắc coding cho dự án **VinFast EV Charging Server**.
> Mọi thành viên (và AI assistant) phải tuân thủ khi viết code.

---

## 1. Quy tắc đặt tên (Naming Convention)

### 1.1. Java Code

| Đối tượng        | Quy tắc           | Ví dụ                                      |
|------------------|--------------------|---------------------------------------------|
| **Package**      | `lowercase`        | `com.vanquy.evcserver.service`              |
| **Class / Interface** | `PascalCase`  | `ChargingStation`, `UserService`            |
| **Method**       | `camelCase`        | `findNearbyStations()`, `calculateDistance()`|
| **Variable**     | `camelCase`        | `stationId`, `totalReviews`                 |
| **Constant**     | `UPPER_SNAKE_CASE` | `MAX_RADIUS_KM`, `DEFAULT_PAGE_SIZE`        |
| **Enum value**   | `UPPER_SNAKE_CASE` | `CCS2`, `CHADEMO`, `TYPE2`                  |

### 1.2. Tên file / class theo vai trò

| Vai trò                | Hậu tố / Tiền tố      | Ví dụ                            |
|------------------------|------------------------|----------------------------------|
| Entity                 | (không có hậu tố)      | `User.java`, `Review.java`      |
| Repository             | `...Repository`        | `UserRepository.java`            |
| Service Interface      | `...Service`           | `UserService.java`               |
| Service Implementation | `...ServiceImpl`       | `UserServiceImpl.java`           |
| Controller             | `...Controller`        | `StationController.java`         |
| Request DTO            | `...Request`           | `RegisterRequest.java`           |
| Response DTO           | `...Response`          | `StationResponse.java`           |
| Exception              | `...Exception`         | `ResourceNotFoundException.java` |
| Config                 | `...Config`            | `SecurityConfig.java`            |

### 1.3. Database

| Đối tượng  | Quy tắc           | Ví dụ                       |
|------------|--------------------|------------------------------|
| Table      | `snake_case` (số nhiều) | `users`, `charging_stations`|
| Column     | `snake_case`       | `user_id`, `full_name`       |
| Index      | `idx_` prefix      | `idx_location`               |
| Unique key | `uq_` prefix       | `uq_user_station_hist`       |

---

## 2. Cấu trúc code theo Layer

### 2.1. Entity (`model/`)

```java
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    // ... các field khác
}
```

**Quy tắc:**
- Luôn dùng `@Column(name = "...")` để map rõ ràng với DB column.
- Relationship: `@ManyToOne(fetch = FetchType.LAZY)` — tránh N+1 query.
- Dùng Lombok `@Data`, `@Builder` để giảm boilerplate.

### 2.2. Repository (`repository/`)

```java
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
```

**Quy tắc:**
- Kế thừa `JpaRepository<Entity, IdType>`.
- Dùng Spring Data method naming cho query đơn giản.
- Dùng `@Query` cho query phức tạp (native hoặc JPQL).
- Return `Optional<T>` cho single result.

### 2.3. Service Interface (`service/`)

```java
public interface UserService {

    UserResponse register(RegisterRequest request);

    UserResponse login(LoginRequest request);

    UserResponse getProfile(Long userId);
}
```

**Quy tắc:**
- Interface chỉ chứa method signature, không có logic.
- Param và return dùng DTO, không dùng Entity.
- Method name mô tả hành động nghiệp vụ (register, login, ...).

### 2.4. Service Implementation (`service/impl/`)

```java
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserResponse register(RegisterRequest request) {
        // 1. Kiểm tra email trùng
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email đã tồn tại");
        }

        // 2. Tạo entity
        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .build();

        // 3. Lưu và trả DTO
        User saved = userRepository.save(user);
        return mapToResponse(saved);
    }
}
```

**Quy tắc:**
- Dùng `@RequiredArgsConstructor` + `final` field thay vì `@Autowired`.
- Dùng `@Transactional` cho method có write operation.
- Business logic NẰM Ở ĐÂY — không ở controller, không ở repository.
- Throw exception cụ thể, không dùng generic `RuntimeException`.

### 2.5. Controller (`controller/`)

```java
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(
            @Valid @RequestBody RegisterRequest request) {
        UserResponse data = userService.register(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Đăng ký thành công", data));
    }
}
```

**Quy tắc:**
- Controller **KHÔNG** chứa business logic — chỉ nhận request, gọi service, trả response.
- Dùng `@Valid` để validate request DTO.
- Trả `ResponseEntity<ApiResponse<T>>` — format nhất quán.
- Dùng đúng HTTP status code (`201` cho tạo mới, `200` cho query/update).

### 2.6. DTO (`dto/request/`, `dto/response/`)

```java
// Request
@Data
public class RegisterRequest {
    @NotBlank(message = "Họ tên không được để trống")
    private String fullName;

    @Email(message = "Email không hợp lệ")
    @NotBlank(message = "Email không được để trống")
    private String email;

    @Size(min = 6, message = "Mật khẩu tối thiểu 6 ký tự")
    private String password;
}

// Response
@Data
@Builder
public class UserResponse {
    private Long userId;
    private String fullName;
    private String email;
    private String token;
}
```

**Quy tắc:**
- Request DTO: dùng Jakarta Validation (`@NotBlank`, `@Email`, `@Size`, ...).
- Response DTO: dùng `@Builder` để tạo dễ dàng.
- **KHÔNG** dùng Entity làm response — luôn map sang DTO.

### 2.7. Exception Handling (`exception/`)

```java
// Custom Exception
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}

// Global Handler
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage()));
    }
}
```

---

## 3. Quy tắc chung

### 3.1. Code Style

- **Indentation:** 4 spaces (không dùng tab).
- **Max line length:** 120 ký tự.
- **Import:** Không dùng wildcard `import *`. Sắp xếp import theo nhóm.
- **Comment:** Viết comment bằng tiếng Việt hoặc tiếng Anh — nhất quán trong cùng file.
- **Magic number:** Dùng constant thay vì hardcode số.

### 3.2. Best Practices

1. **KISS** — Giữ code đơn giản, dễ hiểu.
2. **DRY** — Không lặp lại code. Extract helper method / util nếu cần.
3. **Fail Fast** — Validate đầu vào sớm, throw exception ngay khi phát hiện lỗi.
4. **Logging** — Dùng SLF4J (`@Slf4j` Lombok) cho logging. Không dùng `System.out.println`.
5. **Null Safety** — Dùng `Optional` cho query result. Không return `null` trong service.

### 3.3. Không nên (Anti-patterns)

| ❌ KHÔNG nên                              | ✅ NÊN làm                               |
|------------------------------------------|------------------------------------------|
| `@Autowired` field injection             | `@RequiredArgsConstructor` + `final`     |
| Trả Entity trực tiếp cho client          | Map sang Response DTO                    |
| Logic nghiệp vụ trong Controller         | Đặt logic trong Service layer            |
| Catch Exception rồi bỏ qua              | Log lỗi hoặc throw lại exception khác   |
| Hardcode giá trị                         | Dùng `application.properties` hoặc const |
| `System.out.println`                     | `log.info()`, `log.error()`              |
| Return `null`                            | Return `Optional` hoặc throw exception   |

---

## 4. Cấu hình & Môi trường

- Tất cả giá trị cấu hình (DB credentials, JWT secret, …) đặt trong `application.properties`.
- **KHÔNG** commit thông tin nhạy cảm (password, API key) lên Git.
- Dùng `.env` hoặc Spring Profiles (`application-dev.properties`, `application-prod.properties`) khi cần tách môi trường.
