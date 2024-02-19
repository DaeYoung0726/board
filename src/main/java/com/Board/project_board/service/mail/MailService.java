package com.Board.project_board.service.mail;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
@EnableAsync        // 비동기 처리를 위해.
public class MailService {

    private final JavaMailSender javaMailSender;
    private final String updateTitle = "[board] 회원님의 등급이 업데이트 되었습니다.";
    private final String updateText = "로 업데이트 되었습니다!";
    private final String verifyTitle = "[board] 회원가입 인증 메일입니다.";
    private final String verifyText = "인증번호 = ";
    @Value("${spring.mail.username}")
    private String emailUsername;

    /* 메일 유형 선택 */
    @Async  // 메일 보내기 비동기 처리. 메일을 보내는 동안 다른걸 할 수 있도록.
    public void selectMail(String select, String email, String text) {
        switch (select) {
            case "update" -> sendMail(email, updateTitle, text + updateText);
            case "verify" -> sendMail(email, verifyTitle, verifyText + text);
            default -> throw new IllegalArgumentException("Invalid select value: " + select);
        }
    }

    /* 메일 보내기 */
    private void sendMail(String email, String title, String text) {

        SimpleMailMessage message = getMessage(email, title, text);
        try {
            javaMailSender.send(message);
        } catch (MailException e) {
            log.error("Failed to send email: {}", e.getMessage());
        }
    }

    /* 메일 구성하기 */
    private SimpleMailMessage getMessage(String email, String title, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject(title);
        message.setText(text);
        message.setFrom(emailUsername);     // 네이버는 From 지정안해주면 안보내짐.
        return message;
    }
}
