package com.Board.project_board.config.converter;

import com.Board.project_board.entity.Role;
import org.springframework.core.convert.converter.Converter;

public class StringToRoleConverter implements Converter<String, Role> {

    /* String -> Enum(Role)를 위한 Converter */
    @Override
    public Role convert(String source) {
        try {
            return Role.valueOf(source.toUpperCase());
        } catch (IllegalArgumentException e) {
            // 유효하지 않은 열거형 값일 경우 처리
            return null; // 또는 예외를 throw할 수도 있음
        }
    }
}
