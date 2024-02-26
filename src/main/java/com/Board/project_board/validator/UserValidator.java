package com.Board.project_board.validator;

import com.Board.project_board.dto.UserDto;
import com.Board.project_board.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class UserValidator implements Validator {

    private final UserRepository userRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return UserDto.Request.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {

        UserDto.Request dto = (UserDto.Request) target;
        if(userRepository.existsByUserId(dto.getUserId()))
            errors.rejectValue("userId", "이미 존재하는 아이디 입니다.");
        if(userRepository.existsByEmail(dto.getEmail()))
            errors.rejectValue("email", "이미 존재하는 이메일 입니다.");
        if(userRepository.existsByNickname(dto.getNickname()))
            errors.rejectValue("nickname", "이미 존재하는 닉네임 입니다.");
    }
}
