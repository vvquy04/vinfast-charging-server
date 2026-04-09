# 06 — Hướng dẫn cho AI Assistant

> Tài liệu này dành cho **AI coding assistant** (GitHub Copilot, Gemini, ChatGPT, …) khi được yêu cầu hỗ trợ phát triển dự án **VinFast EV Charging Server**.
> Hãy đọc kỹ trước khi bắt đầu viết code.

---

## 1. Tổng quan dự án

- **Tên:** VinFast EV Charging Station Finder — Backend Server
- **Mục đích:** REST API phục vụ ứng dụng di động tìm kiếm, đánh giá trạm sạc xe điện VinFast
- **Tech stack:** Spring Boot 4.0.5, Java 17, MySQL 8, Hibernate, Lombok, Spring Security
- **Build tool:** Maven
- **Port:** 8080

---

## 2. Tài liệu tham chiếu (BẮT BUỘC đọc)

Trước khi viết bất kỳ dòng code nào, hãy tham khảo các file trong thư mục `docs/`:

| File                    | Nội dung                                          |
|-------------------------|----------------------------------------------------|
| `01_architecture.md`    | Kiến trúc phân lớp, cấu trúc package               |
| `02_database.md`        | Schema database, ERD, SQL khởi tạo                  |
| `03_api_design.md`      | Danh sách endpoint, request/response format          |
| `04_coding_rules.md`    | Quy tắc đặt tên, code style, best practices         |
| `05_git_workflow.md`    | Branching strategy, commit message format            |
| `07_features.md`        | Danh sách tính năng và trạng thái hiện tại           |

---

## 3. Nguyên tắc khi viết code

### 3.1. Cấu trúc bắt buộc

Mọi tính năng mới phải tuân theo kiến trúc phân lớp:

```
Entity (model/)
  → Repository (repository/)
    → Service Interface (service/)
      → Service Implementation (service/impl/)
        → Controller (controller/)
          → DTO (dto/request/, dto/response/)
```

### 3.2. Checklist khi tạo tính năng mới

- [ ] Tạo / cập nhật Entity trong `model/`
- [ ] Tạo Repository interface trong `repository/`
- [ ] Tạo Service interface trong `service/`
- [ ] Tạo ServiceImpl trong `service/impl/`
- [ ] Tạo Request / Response DTO trong `dto/`
- [ ] Tạo Controller trong `controller/`
- [ ] Xử lý exception nếu cần trong `exception/`
- [ ] Cập nhật `07_features.md` với trạng thái mới

### 3.3. Quy tắc code QUAN TRỌNG

1. **Không trả Entity cho client** — Luôn map sang Response DTO.
2. **Dùng constructor injection** — `@RequiredArgsConstructor` + `final`, không dùng `@Autowired`.
3. **Service xử lý logic** — Controller chỉ nhận request và trả response.
4. **Dùng Optional** — Repository trả `Optional<T>`, không return null.
5. **Custom Exception** — Throw `ResourceNotFoundException`, `DuplicateResourceException`, … Không throw generic `RuntimeException`.
6. **Response format nhất quán** — Dùng `ApiResponse<T>` wrapper cho tất cả response.
7. **Validate input** — Dùng `@Valid` + Jakarta Validation annotations trên Request DTO.
8. **Lombok** — Dùng `@Data`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`, `@RequiredArgsConstructor`, `@Slf4j`.

---

## 4. Khi được hỏi, hãy tuân thủ

### 4.1. Khi được yêu cầu tạo endpoint mới

1. Kiểm tra `03_api_design.md` xem endpoint đã được thiết kế chưa.
2. Tuân theo format response đã quy định trong API design.
3. Tạo đầy đủ các layer (Entity → Repository → Service → Controller → DTO).
4. Thêm error handling phù hợp.

### 4.2. Khi được yêu cầu sửa bug

1. Xác định bug nằm ở layer nào.
2. Sửa ở đúng layer — không "hack" ở layer khác.
3. Giải thích nguyên nhân root cause.

### 4.3. Khi được yêu cầu giải thích code

1. Giải thích bằng tiếng Việt (trừ khi được yêu cầu khác).
2. Giải thích theo từng layer, từng method.
3. Nêu rõ data flow từ client đến DB và ngược lại.

---

## 5. Ví dụ template code

### Entity

```java
@Entity
@Table(name = "table_name")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EntityName {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_column")
    private Long id;

    @Column(name = "column_name", nullable = false)
    private String fieldName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_column", nullable = false)
    private RelatedEntity relatedEntity;
}
```

### Service Implementation

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class EntityServiceImpl implements EntityService {

    private final EntityRepository entityRepository;

    @Override
    @Transactional(readOnly = true)
    public EntityResponse getById(Long id) {
        Entity entity = entityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy entity với id: " + id));
        return mapToResponse(entity);
    }

    private EntityResponse mapToResponse(Entity entity) {
        return EntityResponse.builder()
                .id(entity.getId())
                .fieldName(entity.getFieldName())
                .build();
    }
}
```

### Controller

```java
@RestController
@RequestMapping("/api/resources")
@RequiredArgsConstructor
public class EntityController {

    private final EntityService entityService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EntityResponse>> getById(@PathVariable Long id) {
        EntityResponse data = entityService.getById(id);
        return ResponseEntity.ok(ApiResponse.success("OK", data));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<EntityResponse>> create(
            @Valid @RequestBody CreateRequest request) {
        EntityResponse data = entityService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Tạo thành công", data));
    }
}
```

---

## 6. Lưu ý đặc biệt

- **Database schema là source of truth** — Xem `02_database.md` trước khi tạo Entity.
- **API contract là cố định** — Xem `03_api_design.md` trước khi tạo Controller. Nếu cần đổi API, cập nhật doc trước.
- **Không tự ý thêm dependency** — Hỏi trước khi thêm library mới vào `pom.xml`.
- **Comment bằng tiếng Việt OK** — Dự án đồ án tốt nghiệp, comment tiếng Việt được chấp nhận.
- **Test** — Khi được yêu cầu viết test, dùng JUnit 5 + Mockito. Mock service layer khi test controller.
