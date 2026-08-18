package com.meyarc.library.service;

import com.meyarc.library.dto.CategoryRequest;
import com.meyarc.library.dto.CategoryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CategoryService {

    CategoryResponse create(CategoryRequest request);

    CategoryResponse getById(Long id);

    Page<CategoryResponse> getAll(Pageable pageable);

    CategoryResponse update(Long id, CategoryRequest request);

    void delete(Long id);
}
