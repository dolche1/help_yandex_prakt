package ru.practicum.category.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.model.Category;
import ru.practicum.category.model.dto.CategoryDto;
import ru.practicum.category.model.dto.CategoryMapper;
import ru.practicum.category.model.dto.NewCategoryDto;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exceptions.EntityAlreadyExists;
import ru.practicum.exceptions.EntityNotFoundException;

import java.util.List;
import java.util.stream.Collectors;



@Service
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final EventRepository repository;
    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository,EventRepository repository) {
        this.categoryRepository = categoryRepository;
        this.repository = repository;
    }

    @Override
    public CategoryDto getCategoryById(Integer catId) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new EntityNotFoundException("Категория с id=" + catId + " не найдена"));
        return CategoryMapper.categoryToCategoryDto(category);
    }

    @Override
    public List<CategoryDto> getCategories(Integer from, Integer size) {
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size, Sort.by("id"));
        return categoryRepository.findAll(pageable).stream()
                .map(CategoryMapper::categoryToCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CategoryDto addCategory(NewCategoryDto newCategoryDto) {
        try {
            Category category = categoryRepository.save(CategoryMapper.categoryFromNewCategoryDto(newCategoryDto));
            return CategoryMapper.categoryToCategoryDto(category);
        } catch (DataIntegrityViolationException e) {
            throw new EntityAlreadyExists(
                    "Категория " + newCategoryDto.getName() + " уже существует"
            );
        }
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(CategoryDto categoryDto) {
        int catId = categoryDto.getId();
        String name = categoryDto.getName();
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new EntityNotFoundException("Категория с id=" + catId + " не найдена"));
        if (categoryRepository.existsByNameAndIdNot(name, catId))
            throw new EntityAlreadyExists(
                    "Категория " + name + " уже существует"
            );
        category.setName(name);
        categoryRepository.save(category);
        return CategoryMapper.categoryToCategoryDto(category);
    }

    @Override
    @Transactional
    public void deleteCategory(Integer catId) {
        if (!categoryRepository.existsById(catId))
            throw new EntityNotFoundException("Категория с id=" + catId + " не найдена");
        if (repository.countByCategoryId(catId) > 0)
            throw new EntityAlreadyExists("Категория с id=" + catId + " еще используется");
        categoryRepository.deleteById(catId);
    }
}
