package com.Board.project_board.service;

import com.Board.project_board.dto.UserDto;
import com.Board.project_board.entity.User;
import com.Board.project_board.service.mail.MailService;
import com.Board.project_board.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    // 회원가입
    @Transactional
    public Long create(UserDto.Request dto) {
        log.info("Creating user with username: {}", dto.getUsername());
        dto.setPassword(bCryptPasswordEncoder.encode(dto.getPassword()));
        User user = dto.toEntity();

        userRepository.save(user);

        log.info("User created with ID: {}", user.getId());
        return user.getId();
    }

    /* 회원가입 에러 확인. */
    public void validateHandler(Errors errors) {
        for (FieldError error : errors.getFieldErrors()) {
            errors.rejectValue(String.valueOf(error), error.getDefaultMessage());
        }
    }

    /* 사용자 전체 읽기. */
    @Transactional(readOnly = true)
    public List<UserDto.Response> findAll() {

        log.info("Finding all users");
        List<User> users = userRepository.findAll();
        return users.stream().map(UserDto.Response::new).collect(Collectors.toList());
    }

    /* 사용자 읽기. */
    @Transactional(readOnly = true)
    public UserDto.Response findById(Long id) {

        log.info("Finding user by ID: {}", id);
        return new UserDto.Response(userRepository.findById(id).orElse(null));
    }

    /* 아이디를 통한 사용자 읽기. */
    @Transactional(readOnly = true)
    public UserDto.Response findByUsername(String username) {
        log.info("Finding user by Username: {}", username);
        return new UserDto.Response(userRepository.findByUsername(username).orElse(null));
    }

    /* 사용자 업데이트. */
    @Transactional
    public void update(String username, UserDto.UpdateRequest dto) {

        log.info("Updating user with Username: {}", username);
        User user = userRepository.findByUsername(username).orElseThrow(() ->
                new IllegalArgumentException("해당 회원이 존재하지 않습니다. username: " + username));
        String encPassword = bCryptPasswordEncoder.encode(dto.getPassword());
        if(!verifyAuthenticationByUsername(username, user.getUsername()))
            throw new RuntimeException("권한이 없습니다.");
        else {
            user.update(dto.getNickname(), encPassword, dto.getEmail());
            log.info("User updated successfully with Username: {}", username);
        }
    }

    /* 사용자 등급 업데이트 */
    @Transactional
    public String roleUpdate(String username) {

        log.info("Updating user role with username: {}", username);
        User user = userRepository.findByUsername(username).orElseThrow(() ->
                new IllegalArgumentException("해당 회원이 존재하지 않습니다. username: " + username));

        if(user.getRole().getNext() != null)
            user.roleUpdate(user.getRole().getNext());
        log.info("User role updated successfully with username: {}", username);

        return String.valueOf(user.getRole());
    }

    /* 등업 확인을 위한 메서드. */
    @Transactional(readOnly = true)
    public boolean checkRoleUpgrade(String username) {

        UserDto.Response user = findByUsername(username);

        if(user.getRole().getValue().equals("ROLE_BRONZE") && user.getPostSize() >= 1 && user.getCommentSize() >= 2)
            return true;
        if(user.getRole().getValue().equals("ROLE_SILVER") && user.getPostSize() >= 30 && user.getCommentSize() >= 60)
            return true;
        return false;
    }

    /* 자신의 권한인지 확인 */
    private boolean verifyAuthenticationByUsername(String expectedUsername, String actualUsername) {
        return actualUsername.equals(expectedUsername);
    }
}
