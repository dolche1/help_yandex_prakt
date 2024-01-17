package ru.practicum.category.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import ru.practicum.category.model.Category;

@Component
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    Boolean existsByNameAndIdNot(String name, Integer id);
}
