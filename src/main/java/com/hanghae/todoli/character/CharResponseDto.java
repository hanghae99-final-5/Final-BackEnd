package com.hanghae.todoli.character;

import com.hanghae.todoli.equipitem.EquipItemDto;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@Builder
public class CharResponseDto {
    private Boolean matchingState;

    private int level;
    private int hp;
    private int maxHp;
    private int exp;
    private int maxExp;
    private int money;
    private String charImg;
    private String nickname;

    private List<EquipItemDto> equipItems;  //List<EquipItemDto> listDto

}
