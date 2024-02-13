package com.Board.project_board.controller;

import com.Board.project_board.dto.CategoryDto;
import com.Board.project_board.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CategoryController_REST {

    private final CategoryService categoryService;

    /* 카테고리 생성 */
    @PostMapping("/category/create")
    public ResponseEntity<String> create(@RequestBody CategoryDto.Request dto) {
        try {
            categoryService.save(dto);
            return ResponseEntity.ok("카테고리 생성 성공.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("카테고리 생성 실패.");
        }
    }

    /* 카테고리 불러오기. 이름만 가지고 옴. */
    @GetMapping("/category")
    public List<String> findAll() {
        return categoryService.findAll();
    }

    /* 카테고리 업데이트 */
    @PutMapping("/category/{category_name}")
    public ResponseEntity<String> update(@PathVariable String category_name,
                                         @RequestBody CategoryDto.Request dto) {
        try {
            categoryService.update(category_name, dto);
            return ResponseEntity.ok("카테고리 업데이트 성공.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("카테고리 업데이트 실패.");
        }
    }

    /* 카테고리 삭제 */
    @DeleteMapping("/category/{category_name}")
    public ResponseEntity<String> update(@PathVariable String category_name) {
        try {
            categoryService.delete(category_name);
            return ResponseEntity.ok("카테고리 삭제 성공.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("카테고리 삭제 실패.");
        }
    }

}
