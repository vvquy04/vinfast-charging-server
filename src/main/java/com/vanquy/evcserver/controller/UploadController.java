package com.vanquy.evcserver.controller;

import com.vanquy.evcserver.dto.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/uploads")
@RequiredArgsConstructor
public class UploadController {

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    @PostMapping(value = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadAvatar(
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("File ảnh không được để trống"));
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return ResponseEntity.badRequest().body(ApiResponse.error("File phải là hình ảnh"));
        }

        Path baseDir = Paths.get(uploadDir, "avatars").toAbsolutePath().normalize();
        Files.createDirectories(baseDir);

        String originalName = StringUtils.cleanPath(file.getOriginalFilename() == null ? "avatar" : file.getOriginalFilename());
        String extension = "";
        int dotIndex = originalName.lastIndexOf('.');
        if (dotIndex >= 0) {
            extension = originalName.substring(dotIndex);
        }

        String safeFileName = "avatar-"
                + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + "-"
                + UUID.randomUUID()
                + extension;

        Path targetPath = baseDir.resolve(safeFileName).normalize();
        Files.copy(file.getInputStream(), targetPath);

        String publicUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
            .path("/uploads/avatars/")
            .path(safeFileName)
            .toUriString();
        Map<String, String> data = new HashMap<>();
        data.put("url", publicUrl);
        data.put("fileName", safeFileName);

        return ResponseEntity.ok(ApiResponse.success("Tải ảnh lên thành công", data));
    }

    @PostMapping(value = "/station", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadStation(
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("File ảnh không được để trống"));
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return ResponseEntity.badRequest().body(ApiResponse.error("File phải là hình ảnh"));
        }

        Path baseDir = Paths.get(uploadDir, "stations").toAbsolutePath().normalize();
        Files.createDirectories(baseDir);

        String originalName = StringUtils.cleanPath(file.getOriginalFilename() == null ? "station" : file.getOriginalFilename());
        String extension = "";
        int dotIndex = originalName.lastIndexOf('.');
        if (dotIndex >= 0) {
            extension = originalName.substring(dotIndex);
        }

        String safeFileName = "station-"
                + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + "-"
                + UUID.randomUUID()
                + extension;

        Path targetPath = baseDir.resolve(safeFileName).normalize();
        Files.copy(file.getInputStream(), targetPath);

        String publicUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
            .path("/uploads/stations/")
            .path(safeFileName)
            .toUriString();
        Map<String, String> data = new HashMap<>();
        data.put("url", publicUrl);
        data.put("fileName", safeFileName);

        return ResponseEntity.ok(ApiResponse.success("Tải ảnh trạm sạc lên thành công", data));
    }
}
