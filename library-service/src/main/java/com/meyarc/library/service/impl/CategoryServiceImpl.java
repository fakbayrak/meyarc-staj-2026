package com.meyarc.library.service.impl;

import com.meyarc.library.dto.CategoryRequest;
import com.meyarc.library.dto.CategoryResponse;
import com.meyarc.library.entity.Category;
import com.meyarc.library.exception.ResourceNotFoundException;
import com.meyarc.library.mapper.CategoryMapper;
import com.meyarc.library.repository.CategoryRepository;
import com.meyarc.library.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public CategoryResponse create(CategoryRequest request) {
        Category category = categoryMapper.toEntity(request);
        Category saved = categoryRepository.save(category);
        return categoryMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getById(Long id) {
        Category category = findCategoryOrThrow(id);
        return categoryMapper.toResponse(category);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CategoryResponse> getAll(Pageable pageable) {
        return categoryRepository.findAll(pageable)
                .map(categoryMapper::toResponse);
    }

    @Override
    public CategoryResponse update(Long id, CategoryRequest request) {
        Category category = findCategoryOrThrow(id);
        category.setName(request.getName());
        return categoryMapper.toResponse(category);
    }

    @Override
    public void delete(Long id) {
        Category category = findCategoryOrThrow(id);
        categoryRepository.delete(category);
    }

    private Category findCategoryOrThrow(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category bulunamadi, id=" + id));
    }
}
