package ru.practicum.explore.with.me.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.explore.with.me.model.category.Category;
import ru.practicum.explore.with.me.model.category.CategoryDto;
import ru.practicum.explore.with.me.model.category.NewCategoryDto;

@Component
public class CategoryMapper {
    public static Category toModel(NewCategoryDto newCategoryDto) {
        Category category = new Category();
        category.setName(newCategoryDto.getName());
        return category;
    }

    public static CategoryDto toDto(Category category) {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(category.getId());
        categoryDto.setName(category.getName());
        return categoryDto;
    }
}