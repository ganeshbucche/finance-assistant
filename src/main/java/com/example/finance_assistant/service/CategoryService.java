package com.example.finance_assistant.service;

import com.example.finance_assistant.dto.CategoryRequest;
import com.example.finance_assistant.dto.CategoryResponse;
import com.example.finance_assistant.entity.Category;
import com.example.finance_assistant.exception.DuplicateResourceException;
import com.example.finance_assistant.exception.ResourceNotFoundException;
import com.example.finance_assistant.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryResponse create(CategoryRequest request) {
        String name = request.name().trim();

        if (categoryRepository.existsByNameIgnoreCase(name)) {
            throw new DuplicateResourceException("Category already exists: " + name);
        }

        Category category = new Category();
        category.setName(name);
        category.setType(request.type());
        return toResponse(categoryRepository.save(category));
    }

    public List<CategoryResponse> getAll() {
        return categoryRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public CategoryResponse getById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id " + id));
        return toResponse(category);
    }

    private CategoryResponse toResponse(Category category) {
        return new CategoryResponse(category.getId(), category.getName(), category.getType());
    }
}