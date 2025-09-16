package com.example.bankcards.dto.response;


import java.util.List;
import lombok.Builder;

@Builder
public record PageResponse<T>(

    List<T> content,

    Integer currentPage,

    Long totalElements,

    Integer totalPages

) {
    @Override
    public String toString() {
        return "PageResponse{" +
            "currentPage=" + currentPage +
            ", totalElements=" + totalElements +
            ", totalPages=" + totalPages +
            '}';
    }
}
