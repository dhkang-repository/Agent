package org.example.agent.global.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.example.agent.global.constrant.DateTypeEnum;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
public class PageResult<T> {
    @Schema(description = "총 페이지 수", example = "2")
    private int totalPages;
    @Schema(description = "요청 페이지", example = "0")
    private int pageNumber;
    @Schema(description = "페이지 크기", example = "10")
    private int pageSize;
    @Schema(description = "총 요소 갯수", example = "20")
    private long totalElements;
    @Schema(description = "조회 시작날짜", example = "")
    private String startDate;
    @Schema(description = "조회 종료날짜", example = "")
    private String endDate;
    @Schema(description = "데이터 타입", example = "D|W|M")
    private DateTypeEnum type;
    @Schema(description = "페이지 요소", example = "")
    private List<T> elements;

    public PageResult(int totalPages, int pageNumber, int pageSize, long totalElements, DateTypeEnum type, List<T> elements, String startDate, String endDate) {
        this.totalPages = totalPages;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalElements = totalElements;
        this.type = type;
        this.startDate = startDate;
        this.endDate = endDate;
        this.elements = elements;
    }

    public PageResult(int totalPages, int pageNumber, int pageSize, long totalElements, DateTypeEnum type, List<T> elements) {
        this.totalPages = totalPages;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalElements = totalElements;
        this.type = type;
        this.elements = elements;
    }

    public PageResult(int totalPages, int pageNumber, int pageSize, long totalElements, List<T> elements) {
        this.totalPages = totalPages;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalElements = totalElements;
        this.elements = elements;
    }

    public static PageResult of(Page<?> pages, DateTypeEnum type, String startDate, String endDate) {
        int totalPages = pages.getTotalPages();
        int pageNumber = pages.getNumber();
        int pageSize = pages.getSize();
        long totalElements = pages.getTotalElements();

        List elements = pages.getContent();

        return new PageResult(totalPages, pageNumber, pageSize, totalElements, type, elements, startDate, endDate);
    }

    public static PageResult of(Page<?> pages, DateTypeEnum type) {
        int totalPages = pages.getTotalPages();
        int pageNumber = pages.getNumber();
        int pageSize = pages.getSize();
        long totalElements = pages.getTotalElements();
        List elements = pages.getContent();
        return new PageResult(totalPages, pageNumber, pageSize, totalElements, type, elements);
    }

    public static PageResult of(Page<?> pages) {
        int totalPages = pages.getTotalPages();
        int pageNumber = pages.getNumber();
        int pageSize = pages.getSize();
        long totalElements = pages.getTotalElements();
        List elements = pages.getContent();
        return new PageResult(totalPages, pageNumber, pageSize, totalElements, elements);
    }
}
