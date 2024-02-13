package com.Board.project_board.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    GOLD("ROLE_GOLD", null), SILVER("ROLE_SILVER", GOLD), BRONZE("ROLE_BRONZE", SILVER),
    MANAGER("ROLE_MANAGER", null), ADMIN("ROLE_ADMIN", null);

    private final String value;
    private final Role next;

}
