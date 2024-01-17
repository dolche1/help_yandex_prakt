package ru.practicum.category.service;


import ru.practicum.category.model.dto.CategoryDto;
import ru.practicum.category.model.dto.NewCategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto getCategoryById(Integer catId);

    List<CategoryDto> getCategories(Integer from, Integer size);

    CategoryDto addCategory(NewCategoryDto newCategoryDto);

    CategoryDto updateCategory(CategoryDto categoryDto);

    void deleteCategory(Integer catId);
}