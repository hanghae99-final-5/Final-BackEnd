package com.hanghae.todoli.models;

import javax.persistence.*;

@Entity
public class Alarm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String message;

    @Column
    private String alarmDate;

    @Column
    private Long senderId;
}
