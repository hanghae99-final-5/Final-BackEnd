package com.hanghae.todoli.models;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor
public class Todo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String proofImg;

    @Column
    private String content;

    @Column
    private String startDate;

    @Column
    private String endDate;

    @Column
    private int difficulty;

    @Column
    private Boolean confirmState;

    @Column
    private Boolean complitionState;


}
