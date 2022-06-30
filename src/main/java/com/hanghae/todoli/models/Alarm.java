package com.hanghae.todoli.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

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
    private String alarmDate;

    @Column
    private Long senderId;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;
}
