package com.hanghae.todoli.member;

import com.hanghae.todoli.character.Character;
import com.hanghae.todoli.character.CharacterRepository;
import com.hanghae.todoli.equipitem.EquipItem;
import com.hanghae.todoli.equipitem.EquipItemRepository;
import com.hanghae.todoli.exception.CustomException;
import com.hanghae.todoli.exception.ErrorCode;
import com.hanghae.todoli.inventory.Inventory;
import com.hanghae.todoli.inventory.InventoryRepository;
import com.hanghae.todoli.item.Item;
import com.hanghae.todoli.item.ItemRepository;
import com.hanghae.todoli.item.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final CharacterRepository characterRepository;
    private final EquipItemRepository equipItemRepository;
    private final ItemRepository itemRepository;
    private final InventoryRepository inventoryRepository;

    //회원가입
    @Transactional
    public void signup(SignupRequestDto signupRequestDto) {
        String username = signupRequestDto.getUsername();
        String nickname = signupRequestDto.getNickname();

        idCheck(username);

        String password = passwordEncoder.encode(signupRequestDto.getPassword());

        //멤버생성과 동시에 캐릭터, 장착아이템 같이 생성, 기본아이템 제공 및 장착까지
        Item basicAccessory = itemRepository.findById(1L).orElseThrow(
                () -> new CustomException(ErrorCode.NO_ITEM)
        );
        Item basicHair = itemRepository.findById(2L).orElseThrow(
                () -> new CustomException(ErrorCode.NO_ITEM)
        );
        Item basicCloth = itemRepository.findById(3L).orElseThrow(
                () -> new CustomException(ErrorCode.NO_ITEM)
        );
        EquipItem equipItem = new EquipItem(1L,2L,3L);
        equipItemRepository.save(equipItem);
        Character character = new Character(equipItem);
        characterRepository.save(character);
        Inventory addAccessory = new Inventory(basicAccessory, character);
        inventoryRepository.save(addAccessory);
        Inventory addHair = new Inventory(basicHair, character);
        inventoryRepository.save(addHair);
        Inventory addCloth = new Inventory(basicCloth, character);
        inventoryRepository.save(addCloth);

        Member Member = new Member(username, nickname, password, false, character);
        memberRepository.save(Member);

    }


    @Transactional
    public Member login(LoginRequestDto loginRequestDto) {
        String username = loginRequestDto.getUsername();
        Member Member = memberRepository.findByUsername(username).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_MEMBER)
        );

        if (!passwordEncoder.matches(loginRequestDto.getPassword(), Member.getPassword())) {
            throw new CustomException(ErrorCode.PASSWORD_NOT_SAME);
        }
        return Member;
    }

    //아이디 중복확인
    private void idCheck(String username) {
        Optional<Member> found = memberRepository.findByUsername(username);
        if (found.isPresent()) {
            throw new CustomException(ErrorCode.DUPLICATED_ID);
        }
    }
}
