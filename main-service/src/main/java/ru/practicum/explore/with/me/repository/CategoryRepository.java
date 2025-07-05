package ru.practicum.explore.with.me.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.explore.with.me.model.category.Category;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query("SELECT COUNT(c) = 1 FROM Category c WHERE c.name = :name")
    boolean isExistName(@Param("name") String name);

    @Query("SELECT DISTINCT c FROM Category c ORDER BY c.id ASC LIMIT :size OFFSET :from")
    List<Category> findCategoriesWitOffsetAndLimit(@Param("from") int from, @Param("size") int size);
}