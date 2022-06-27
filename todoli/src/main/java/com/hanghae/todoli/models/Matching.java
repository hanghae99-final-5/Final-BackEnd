package com.hanghae.todoli.models;

import javax.persistence.*;

@Entity
public class Matching {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long requesterId;

    @Column
    private Long respondentId;
}
