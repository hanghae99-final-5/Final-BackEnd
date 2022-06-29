package com.hanghae.todoli.models;

import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
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
