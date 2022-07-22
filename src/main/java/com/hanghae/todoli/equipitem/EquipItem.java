package com.hanghae.todoli.equipitem;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EquipItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long accessoryId;

    @Column
    private Long hairId;

    @Column
    private Long clothId;

    public void updateAccessory(Long accessoryId) {
        this.accessoryId = accessoryId;
    }

    public void updateHair(Long hairId) {
        this.hairId = hairId;
    }

    public void updateCloth(Long clothId) {
        this.clothId = clothId;
    }

    public EquipItem(Long accessoryId, Long hairId, Long clothId) {
        this.accessoryId = accessoryId;
        this.hairId = hairId;
        this.clothId = clothId;
    }
}
