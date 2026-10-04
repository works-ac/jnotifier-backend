package com.jnotifier.payload.request;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public class AppSearchRequest {
    private Integer page;
    private Integer size;

    @NotNull
    private List<FilterDTO> filters;

    public AppSearchRequest() {
    }

    public AppSearchRequest(Integer page, Integer size, List<FilterDTO> filters) {
        this.page = page;
        this.size = size;
        this.filters = filters;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public List<FilterDTO> getFilters() {
        return filters;
    }

    public void setFilters(List<FilterDTO> filters) {
        this.filters = filters;
    }
}
