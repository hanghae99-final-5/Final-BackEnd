package com.hanghae.todoli.models;

import javax.persistence.*;

@Entity
public class EquipItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long hatId;

    @Column
    private Long accessoryId;

    @Column
    private Long hairId;

    @Column
    private Long clothId;
}
