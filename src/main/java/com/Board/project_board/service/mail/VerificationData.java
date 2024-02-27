package com.Board.project_board.service.mail;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class VerificationData {

    private final String code;
    private final LocalDateTime timestamp;
}
