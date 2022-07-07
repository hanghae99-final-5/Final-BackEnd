package com.hanghae.todoli.alarm;

import com.hanghae.todoli.member.Member;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Setter
@Getter
public class Alarm{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String message;

    @Column
    private LocalDate alarmDate;

    @Column
    private Long senderId;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;
}
