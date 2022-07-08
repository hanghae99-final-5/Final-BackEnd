package com.hanghae.todoli.alarm;

import com.hanghae.todoli.member.Member;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
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

    @Column
    private Long alarmState = 0L;

    @Column
    private Long todoId;

    @Column
    @Enumerated(EnumType.STRING)
    private AlarmType alarmType;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;
}
