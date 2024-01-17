package ru.practicum.category.model.dto;

import ru.practicum.category.model.Category;

public class CategoryMapper {
    public static Category categoryFromNewCategoryDto(NewCategoryDto newCategoryDto) {
        return Category.builder()
                .id(null)
                .name(newCategoryDto.getName()).
                build();
    }

    public static Category categoryFromCategoryDto(CategoryDto categoryDto) {
        return Category.builder()
                .id(categoryDto.getId())
                .name(categoryDto.getName()).
                build();
    }

    public static CategoryDto categoryToCategoryDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName()).
                build();
    }
}