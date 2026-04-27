package main.dto;

import java.util.List;

public record PaginatedResponse<T>(
    List<T> data,
    int page,
    int limit,
    long totalItems,
    int totalPages
) {
    public PaginatedResponse(List<T> data, int page, int limit, long totalItems) {
        this(
            data, 
            page, 
            limit, 
            totalItems, 
            (int) Math.ceil((double) totalItems / limit)
        );
    }
}
