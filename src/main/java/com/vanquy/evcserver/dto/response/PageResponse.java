package com.vanquy.evcserver.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Generic paginated response — dùng chung cho tất cả API trả danh sách.
 *
 * Format:
 * {
 *   "content": [...],
 *   "totalElements": 45,
 *   "totalPages": 3,
 *   "currentPage": 0
 * }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageResponse<T> {

    private List<T> content;
    private long totalElements;
    private int totalPages;
    private int currentPage;
}
