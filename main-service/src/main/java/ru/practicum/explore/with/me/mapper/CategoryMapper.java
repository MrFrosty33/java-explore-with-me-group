package ru.practicum.explore.with.me.mapper;

import org.mapstruct.Mapper;
import ru.practicum.explore.with.me.model.category.Category;
import ru.practicum.explore.with.me.model.category.CategoryDto;
import ru.practicum.explore.with.me.model.category.NewCategoryDto;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    Category toModel(NewCategoryDto newCategoryDto);

    CategoryDto toDto(Category category);
}