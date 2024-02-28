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
        log.info("Creating user with username: {}", dto.getUserId());
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
        return new UserDto.Response(userRepository.findByUserId(username).orElse(null));
    }

    /* 사용자 업데이트. */
    @Transactional
    public void update(String username, UserDto.UpdateRequest dto) {

        log.info("Updating user with Username: {}", username);
        User user = userRepository.findByUserId(username).orElseThrow(() ->
                new IllegalArgumentException("해당 회원이 존재하지 않습니다. username: " + username));
        String encPassword = bCryptPasswordEncoder.encode(dto.getPassword());
        user.update(dto.getNickname(), encPassword, dto.getEmail());
        log.info("User updated successfully with Username: {}", username);
    }

    /* 사용자 등급 업데이트 */
    @Transactional
    public void roleUpdate(Long id) {

        log.info("Updating user role with ID: {}", id);
        User user = userRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("해당 회원이 존재하지 않습니다."));

        if(user.getRole().getNext() != null)
            user.roleUpdate(user.getRole().getNext());
        log.info("User role updated successfully with ID: {}", id);
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
