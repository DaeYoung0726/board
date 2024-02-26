package com.Board.project_board.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// 특정 필드들은 외부에서 직접적으로 접근할 수 없도록 설정되어 있습니다. 이는 데이터베이스의 일관성과 무결성을 유지하기 위한 것입니다.
@Getter
@MappedSuperclass  // 해당 클래스를 상속받는 서브 클래스에게 매핑 정보를 제공하는 역할. 공통적인 매핑 정보를 담음.
@EntityListeners(AuditingEntityListener.class)  // @EntityListeners : 엔티티의 생명주기 이벤트에 대한 리스너를 지정
// AuditingEntityListener.class : 생성일자와 수정일자를 자동으로 관리하기 위한 리스너. 자동으로 생성일과 수정일을 갱신
abstract class BaseTimeEntity {

    @NotBlank
    @Column(name = "created_date")
    @CreatedDate
    private String created_time;

    @NotBlank
    @Column(name = "modified_date")
    @LastModifiedDate
    private String modified_time;

    /* 해당 엔티티를 저장하기 이전에 실행 */
    @PrePersist
    public void onPrePersist() {
        this.created_time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss"));
        this.modified_time = this.created_time;
    }

    /* 해당 엔티티를 업데이트 하기 이전에 실행*/
    @PreUpdate
    public void onPreUpdate() {
        this.modified_time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss"));
    }
}
