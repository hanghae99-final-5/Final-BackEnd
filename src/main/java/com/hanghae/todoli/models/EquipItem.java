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

    public void updateHat(Long hatId){
        this.hatId = hatId;
    }
    public void updateAccessory(Long accessoryId){
        this.accessoryId = accessoryId;
    }
    public void updateHair(Long hairId){
        this.hairId = hairId;
    }
    public void updateCloth(Long clothId){
        this.clothId = clothId;
    }
}
