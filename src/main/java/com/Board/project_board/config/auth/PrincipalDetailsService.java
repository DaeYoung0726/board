package com.Board.project_board.config.auth;

import com.Board.project_board.entity.User;
import com.Board.project_board.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrincipalDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if(username == null || username.equals("")) {
            throw new UsernameNotFoundException(username);
        }
        User userEntity = userRepository.findByUserId(username).orElse(null);
        if(userEntity == null) {
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
        }
        return new PrincipalDetails(userEntity);
    }
}