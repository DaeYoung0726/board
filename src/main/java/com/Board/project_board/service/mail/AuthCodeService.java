package com.Board.project_board.service.mail;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthCodeService {


    private final MailService mailService;
    private final Map<String, VerificationData> codeMap = new ConcurrentHashMap<>();  // 메일 인증번호 확인용. 멀티스레드 측면에서 안전성 이점.
    private static final int EXPIRATION_MINUTES = 3; // 유효 시간 3분

    /* 회원가입 이메일 인증 번호. */
    public void sendCodeToMail(String email) {
        String code = createCode();
        mailService.selectMail("verify", email, code);
        codeMap.put(email, new VerificationData(code, LocalDateTime.now()));
    }

    /* 인증번호 만드는 메서드. */
    private String createCode() {

        try {
            Random random = SecureRandom.getInstanceStrong();   // 암호학적으로 안전한 무작위 수를 생성. 인증번호는 보안적으로 중요하기 SecureRandom 사용.
            StringBuilder sb = new StringBuilder();
            for(int i = 0; i < 6; i++) {
                sb.append(random.nextInt(10));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            log.info("Failed to create secure random instance", e);
            throw new RuntimeException("Failed to generate secure random number", e);
        }
    }

    /* 인증번호 확인 메서드. */
    public boolean verifiedCode(String email, String code) {
        VerificationData storedCode = codeMap.get(email);
        if(storedCode != null && storedCode.getCode().equals(code)) {
            LocalDateTime expirationTime = storedCode.getTimestamp().plusMinutes(EXPIRATION_MINUTES);

            if (LocalDateTime.now().isBefore(expirationTime)) {     // 유효 시간이 지나지 않았다면
                codeMap.remove(email);      // 인증코드가 맞다면 인증코드 삭제.
                return true;
            } else {
                codeMap.remove(email); // 유효 기간이 지났으면 해당 데이터 삭제
            }
        }
        return false;
    }
}
