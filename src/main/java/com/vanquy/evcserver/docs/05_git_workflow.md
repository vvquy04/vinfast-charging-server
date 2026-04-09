# 05 — Git Workflow

> Quy tắc quản lý Git cho dự án **VinFast EV Charging Server**.
> Áp dụng mô hình **Git Flow đơn giản** phù hợp cho đồ án cá nhân / nhóm nhỏ.

---

## 1. Chiến lược nhánh (Branching Strategy)

```
main ──────────────────────────────────────────── (production-ready)
  │
  └── develop ─────────────────────────────────── (tích hợp hàng ngày)
        │
        ├── feature/user-auth ─────── (merge xong xóa)
        ├── feature/station-search ── (merge xong xóa)
        ├── fix/haversine-bug ──────── (merge xong xóa)
        └── ...
```

### Mô tả từng nhánh

| Nhánh                       | Mục đích                                                   | Tạo từ    | Merge vào  |
|-----------------------------|-------------------------------------------------------------|-----------|------------|
| `main`                      | Code ổn định, chỉ merge khi hoàn thành tính năng lớn / demo | `develop` | —          |
| `develop`                   | Nhánh làm việc chính, tích hợp tất cả feature               | `main`    | `main`     |
| `feature/<tên-tính-năng>`   | Phát triển tính năng mới                                     | `develop` | `develop`  |
| `fix/<tên-bug>`             | Sửa lỗi                                                     | `develop` | `develop`  |

---

## 2. Quy trình làm việc (Workflow)

### 2.1. Bắt đầu tính năng mới

```bash
# 1. Chuyển sang develop và pull code mới nhất
git checkout develop
git pull origin develop

# 2. Tạo nhánh feature mới
git checkout -b feature/ten-tinh-nang

# 3. Code, commit thường xuyên (xem mục 3)
git add .
git commit -m "feat: add user registration endpoint"

# 4. Push nhánh lên remote
git push origin feature/ten-tinh-nang
```

### 2.2. Hoàn thành tính năng → merge vào develop

```bash
# 1. Cập nhật develop mới nhất vào nhánh feature
git checkout develop
git pull origin develop
git checkout feature/ten-tinh-nang
git merge develop

# 2. Giải quyết conflict (nếu có), test lại

# 3. Merge vào develop
git checkout develop
git merge feature/ten-tinh-nang

# 4. Push develop
git push origin develop

# 5. Xóa nhánh feature (đã merge xong)
git branch -d feature/ten-tinh-nang
git push origin --delete feature/ten-tinh-nang
```

### 2.3. Release → merge develop vào main

```bash
# Khi develop đã ổn định, test đầy đủ
git checkout main
git pull origin main
git merge develop
git push origin main

# (Tùy chọn) Tạo tag cho version
git tag -a v1.0.0 -m "Release v1.0.0 - MVP"
git push origin v1.0.0
```

---

## 3. Quy tắc viết Commit Message

### Format

```
<type>: <mô tả ngắn gọn bằng tiếng Anh>
```

- Tối đa **72 ký tự** cho dòng đầu tiên.
- Viết bằng **tiếng Anh**, ở dạng **mệnh lệnh** (ví dụ: "add", không phải "added").
- Chữ thường (không viết hoa chữ cái đầu mô tả).

### Các loại type

| Type         | Ý nghĩa                              | Ví dụ                                    |
|--------------|---------------------------------------|------------------------------------------|
| `feat`       | Thêm tính năng mới                    | `feat: add station search endpoint`      |
| `fix`        | Sửa lỗi                              | `fix: correct haversine distance calc`   |
| `refactor`   | Tái cấu trúc code (không đổi logic)  | `refactor: extract rating calculation`   |
| `docs`       | Cập nhật tài liệu                    | `docs: update API design document`       |
| `chore`      | Cấu hình, setup, dependency          | `chore: add spring-security dependency`  |
| `style`      | Format code, sửa lỗi chính tả        | `style: fix indentation in UserService`  |
| `test`       | Thêm / sửa test                      | `test: add unit test for ReviewService`  |

### Ví dụ commit tốt ✅ vs xấu ❌

| ✅ Tốt                                        | ❌ Xấu                              |
|-----------------------------------------------|--------------------------------------|
| `feat: add user registration endpoint`        | `update code`                        |
| `fix: handle null pointer in station query`   | `fix bug`                            |
| `refactor: split StationService into modules` | `refactor`                           |
| `docs: add database schema documentation`     | `add docs`                           |
| `chore: configure CORS for mobile app`        | `config`                             |

---

## 4. Quy tắc khác

### 4.1. `.gitignore`

Đảm bảo `.gitignore` bao gồm:

```gitignore
# Build
target/

# IDE
.idea/
*.iml
.vscode/
.settings/
.project
.classpath

# OS
.DS_Store
Thumbs.db

# Sensitive
application-prod.properties
.env
```

### 4.2. Không commit lên Git

- ❌ File build (`target/`)
- ❌ File IDE (`.idea/`, `.vscode/`)
- ❌ Thông tin nhạy cảm (password, API key, JWT secret)
- ❌ File log / database dump
- ❌ File binary lớn (image, video)

### 4.3. Commit thường xuyên

- Commit **mỗi khi hoàn thành 1 đơn vị công việc nhỏ** (1 method, 1 endpoint, 1 fix).
- Không gom tất cả thay đổi vào 1 commit lớn.
- Mỗi commit nên **build được** — không commit code bị lỗi.

---

## 5. Ví dụ luồng hoàn chỉnh

```
Tình huống: Thêm tính năng đánh giá trạm sạc

1. git checkout develop && git pull
2. git checkout -b feature/station-reviews
3. Viết code:
   - commit: "feat: add Review entity and repository"
   - commit: "feat: add ReviewService with create and list"
   - commit: "feat: add ReviewController endpoints"
   - commit: "test: add unit test for ReviewService"
   - commit: "docs: update API design with review endpoints"
4. git checkout develop && git pull && git checkout feature/station-reviews && git merge develop
5. Test lại → OK
6. git checkout develop && git merge feature/station-reviews
7. git push origin develop
8. git branch -d feature/station-reviews
```
