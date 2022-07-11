package com.hanghae.todoli.todo.model;

import com.hanghae.todoli.member.Member;
import com.hanghae.todoli.todo.dto.TodoRegisterDto;
import com.hanghae.todoli.utils.Timestamped;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter
@Entity
@NoArgsConstructor
public class Todo extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 투두 완료 인증 사진
    @Column
    private String proofImg;

    // 투두 내용
    @Column
    private String content;

    // 투두 시작일
    @Column
    private LocalDate startDate;

    // 투두 종료일
    @Column
    private LocalDate endDate;

    // 인증 가능 날짜
    @Column
    private LocalDate confirmDate;

    // 투두 난이도
    @Column
    private int difficulty;

    /*
    투두가 개인투두인지, 매칭투두인지 체크
    1: 개인
    2: 매칭
    */
    @Column
    private int todoType;

    // 파트너의 인증 완료 상태
    @Column
    private Boolean confirmState = false;

    // 투두 완료 상태
    @Column
    private Boolean completionState = false;

    @ManyToOne
    @JoinColumn(name = "MEMBER_ID")
    private Member writer;

    public Todo(Member member, TodoRegisterDto registerDto) {
        this.writer = member;
        this.content = registerDto.getContent();
        this.startDate = registerDto.getStartDate();
        this.endDate = registerDto.getEndDate();
        this.todoType = registerDto.getTodoType();
        this.difficulty = registerDto.getDifficulty();
    }

    public void completionState(){
        this.completionState = true;
    }

    // Setter
    public void setProofImg(String proofImg) {
        this.proofImg = proofImg;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public void setConfirmDate(LocalDate confirmDate) {
        this.confirmDate = confirmDate;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public void setConfirmState(Boolean confirmState) {
        this.confirmState = confirmState;
    }

    public void setCompletionState(Boolean completionState) {
        this.completionState = completionState;
    }

    public void setWriter(Member writer) {
        this.writer = writer;
    }

    public void setTodoType(int todoType) {
        this.todoType = todoType;
    }

    public void update(Member member, TodoRegisterDto registerDto) {
        this.writer = member;
        this.content = registerDto.getContent();
        this.startDate = registerDto.getStartDate();
        this.endDate = registerDto.getEndDate();
        this.todoType = registerDto.getTodoType();
        this.difficulty = registerDto.getDifficulty();
        this.confirmDate = registerDto.getEndDate();
    }
}
