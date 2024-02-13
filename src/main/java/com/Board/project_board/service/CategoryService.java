package com.Board.project_board.service;

import com.Board.project_board.dto.CategoryDto;
import com.Board.project_board.entity.Category;
import com.Board.project_board.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    /* 게시판 카테고리 생성 */
    @Transactional
    public Long save(CategoryDto.Request dto) {
        Category category = dto.toEntity();

        categoryRepository.save(category);
        return category.getId();
    }

    /* 게시판 전체 카테고리 불러오기. 이름만 가져오게. */
    @Transactional(readOnly = true)
    public List<String> findAll() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream().map(category -> category.getName()).collect(Collectors.toList());
    }

    /* 게시판 카테고리 업데이트 */
    @Transactional
    public void update(String name, CategoryDto.Request dto) {
        Category category = categoryRepository.findByName(name).orElseThrow(() ->
                new IllegalArgumentException("해당 카테고리가 존재하지 않습니다. name: " + name));

        category.update(dto.getName());
    }

    /* 게시판 카테고리 삭제 */
    @Transactional
    public void delete(String name) {
        Category category = categoryRepository.findByName(name).orElseThrow(() ->
                new IllegalArgumentException("해당 카테고리가 존재하지 않습니다. name: " + name));

        categoryRepository.delete(category);
    }

}
