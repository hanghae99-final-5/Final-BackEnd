package com.hanghae.todoli.models;

import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
public class Matching {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long requesterId;

    @Column
    private Long respondentId;
}
