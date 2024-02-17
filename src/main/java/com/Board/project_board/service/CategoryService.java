package com.Board.project_board.service;

import com.Board.project_board.dto.CategoryDto;
import com.Board.project_board.entity.Category;
import com.Board.project_board.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {

    private final CategoryRepository categoryRepository;

    /* 게시판 카테고리 생성 */
    @Transactional
    public Long save(CategoryDto.Request dto) {

        log.info("Creating category: {}", dto.getName());
        Category category = dto.toEntity();

        categoryRepository.save(category);
        log.info("Category created with ID: {}", category.getId());
        return category.getId();
    }

    /* 게시판 전체 카테고리 불러오기. 이름만 가져오게. */
    @Transactional(readOnly = true)
    public List<String> findAll() {

        log.info("Finding all categories");
        List<Category> categories = categoryRepository.findAll();
        return categories.stream().map(category -> category.getName()).collect(Collectors.toList());
    }

    /* 게시판 카테고리 업데이트 */
    @Transactional
    public void update(String name, CategoryDto.Request dto) {

        log.info("Updating category: {}", name);
        Category category = categoryRepository.findByName(name).orElseThrow(() ->
                new IllegalArgumentException("해당 카테고리가 존재하지 않습니다. name: " + name));

        category.update(dto.getName());
        log.info("Category updated: {}", category);
    }

    /* 게시판 카테고리 삭제 */
    @Transactional
    public void delete(String name) {

        log.info("Deleting category: {}", name);
        Category category = categoryRepository.findByName(name).orElseThrow(() ->
                new IllegalArgumentException("해당 카테고리가 존재하지 않습니다. name: " + name));

        categoryRepository.delete(category);
        log.info("Category deleted: {}", category);
    }

}
