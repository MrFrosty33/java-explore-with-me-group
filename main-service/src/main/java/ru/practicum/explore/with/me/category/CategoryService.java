package ru.practicum.explore.with.me.category;

import ru.practicum.explore.with.me.category.dto.CategoryDto;
import ru.practicum.explore.with.me.category.dto.NewCategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto createCategory(NewCategoryDto categoryDto);

    void deleteCategory(long id);

    CategoryDto updateCategory(long id, NewCategoryDto categoryDto);

    CategoryDto getCategory(long id);

    List<CategoryDto> getCategories(int from, int size);
}
