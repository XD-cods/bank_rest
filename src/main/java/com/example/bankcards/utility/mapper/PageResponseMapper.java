package com.example.bankcards.utility.mapper;

import com.example.bankcards.dto.response.PageResponse;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.springframework.data.domain.Page;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface PageResponseMapper {

    default <T, R> PageResponse<R> toPageResponse(Page<T> sourcePage, int currentPage, Function<T, R> mapper) {
        List<R> content = sourcePage.getContent()
            .stream()
            .map(mapper)
            .collect(Collectors.toList());

        return PageResponse.<R>builder()
            .content(content)
            .currentPage(currentPage)
            .totalElements(sourcePage.getTotalElements())
            .totalPages(sourcePage.getTotalPages())
            .build();
    }

}
