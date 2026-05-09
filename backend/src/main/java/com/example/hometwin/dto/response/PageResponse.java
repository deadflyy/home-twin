package com.example.hometwin.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {

    private List<T> content;
    private long totalElements;
    private int totalPages;
    private int currentPage;

    public static <E, T> PageResponse<T> fromPage(Page<E> page, Function<E, T> converter) {
        List<T> content = page.getContent().stream()
            .map(converter)
            .toList();
        
        return new PageResponse<>(
            content,
            page.getTotalElements(),
            page.getTotalPages(),
            page.getNumber()
        );
    }
}
