package com.Board.project_board.service;

import com.Board.project_board.dto.PostDto;
import com.Board.project_board.dto.UserDto;
import com.Board.project_board.entity.Post;
import com.Board.project_board.entity.User;
import com.Board.project_board.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    // 회원가입
    @Transactional
    public Long create(UserDto.Request dto) {
        dto.setPassword(bCryptPasswordEncoder.encode(dto.getPassword()));
        User user = dto.toEntity();

        userRepository.save(user);
        return user.getId();
    }

    /* 회원가입 에러 확인. */
    public Map<String, String> validateHandler(Errors errors) {
        Map<String, String> validateResult = new HashMap<>();

        for (FieldError error : errors.getFieldErrors()) {
            String validKeyName = "valid_" + error.getField();
            validateResult.put(validKeyName, error.getDefaultMessage());
        }
        return validateResult;
    }

    /* 아이디 중복 확인. */
    @Transactional(readOnly = true)
    public boolean existsByUserId(String userId) {
        return userRepository.existsByUserId(userId);
    }

    /* 닉네임 중복 확인. */
    @Transactional(readOnly = true)
    public boolean existsByNickname(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    /* 이메일 중복 확인 */
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    /* 사용자 전체 읽기. 번호, 이름, 아이디, 닉네임, 이메일, 권한만 가지고 옴. */
    @Transactional(readOnly = true)
    public List<UserDto.Response> findAll() {
        List<User> users = userRepository.findAll();
        return users.stream().map(UserDto.Response::new).collect(Collectors.toList());
    }

    /* 사용자 읽기. */
    @Transactional(readOnly = true)
    public UserDto.Response findById(Long id) {
        return new UserDto.Response(userRepository.findById(id).orElse(null));
    }

    /* 사용자 업데이트. */
    @Transactional
    public void update(Long id, UserDto.Request dto) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("해당 회원이 존재하지 않습니다."));
        String encPassword = bCryptPasswordEncoder.encode(dto.getPassword());
        user.update(dto.getNickname(), encPassword, dto.getEmail());
    }

    /* 사용자 등급 업데이트 */
    @Transactional
    public void roleUpdate(Long id) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("해당 회원이 존재하지 않습니다."));

        if(user.getRole().getNext() != null)
            user.roleUpdate(user.getRole().getNext());
    }

    /* 등업 확인을 위한 메서드. */
    public boolean checkRoleUpgrade(UserDto.Response dto) {
        if(dto.getRole().getValue().equals("ROLE_BRONZE") && dto.getPostSize() >= 10 && dto.getCommentSize() >= 20)
            return true;
        if(dto.getRole().getValue().equals("ROLE_SILVER") && dto.getPostSize() >= 30 && dto.getCommentSize() >= 60)
            return true;
        return false;
    }
}
