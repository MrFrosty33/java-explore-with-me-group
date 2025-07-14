package ru.practicum.explore.with.me.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.explore.with.me.exception.ConflictException;
import ru.practicum.explore.with.me.exception.NotFoundException;
import ru.practicum.explore.with.me.mapper.CategoryMapper;
import ru.practicum.explore.with.me.model.category.Category;
import ru.practicum.explore.with.me.model.category.CategoryDto;
import ru.practicum.explore.with.me.model.category.NewCategoryDto;
import ru.practicum.explore.with.me.repository.CategoryRepository;
import ru.practicum.explore.with.me.util.DataProvider;
import ru.practicum.explore.with.me.util.ExistenceValidator;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements ExistenceValidator<Category>,
        CategoryService, DataProvider<CategoryDto, Category> {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public void validateExists(Long id) {
        if (!categoryRepository.existsById(id)) {
            log.info("Category with id {} not found", id);
            throw new NotFoundException("The required object was not found.",
                    "Category with id=" + id + " was not found");
        }
    }

    @Override
    public CategoryDto getDto(Category entity) {
        return categoryMapper.toDto(entity);
    }

    private void validateNameUnique(String categoryName) {
        if (categoryRepository.isExistName(categoryName)) {
            log.info("Category with name {} already exists", categoryName);
            throw new ConflictException("The name of category should be unique.",
                    "Category with name=" + categoryName + " is already exist");
        }
    }

    @Override
    public CategoryDto createCategory(NewCategoryDto categoryDto) {
        validateNameUnique(categoryDto.getName());
        Category category = categoryRepository.save(categoryMapper.toModel(categoryDto));
        return categoryMapper.toDto(category);
    }

    @Override
    public void deleteCategory(long id) {
        validateExists(id);
        categoryRepository.deleteById(id);
    }

    @Override
    public CategoryDto updateCategory(long id, NewCategoryDto categoryDto) {
        Category categoryToUpdate = categoryRepository.findById(id).orElseThrow(
                () -> new NotFoundException("The required object was not found.",
                        "Category with id=" + id + " was not found")
        );
        validateNameUnique(categoryDto.getName());
        categoryToUpdate.setName(categoryDto.getName());
        Category category = categoryRepository.save(categoryToUpdate);
        return categoryMapper.toDto(category);
    }

    @Override
    public CategoryDto getCategory(long id) {
        Category category = categoryRepository.findById(id).orElseThrow(
                () -> new NotFoundException("The required object was not found.",
                        "Category with id=" + id + " was not found")
        );
        return categoryMapper.toDto(category);
    }

    @Override
    public List<CategoryDto> getCategories(int from, int size) {
        Pageable pageable = PageRequest.of(from, size, Sort.by("id").ascending());
        return categoryRepository.findAllDistinct(pageable).getContent()
                .stream().map(categoryMapper::toDto).toList();
    }
}