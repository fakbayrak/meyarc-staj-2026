package com.meyarc.library.mapper;

import com.meyarc.library.dto.CategoryRequest;
import com.meyarc.library.dto.CategoryResponse;
import com.meyarc.library.entity.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public Category toEntity(CategoryRequest request) {
        Category category = new Category();
        category.setName(request.getName());
        return category;
    }

    public CategoryResponse toResponse(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName()
        );
    }
}
