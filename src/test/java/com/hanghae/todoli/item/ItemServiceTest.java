package com.hanghae.todoli.item;

import com.hanghae.todoli.character.Character;
import com.hanghae.todoli.character.CharacterImg;
import com.hanghae.todoli.character.CharacterRepository;
import com.hanghae.todoli.equipitem.EquipItem;
import com.hanghae.todoli.equipitem.EquipItemDto;
import com.hanghae.todoli.equipitem.EquipItemRepository;
import com.hanghae.todoli.exception.CustomException;
import com.hanghae.todoli.inventory.Inventory;
import com.hanghae.todoli.inventory.InventoryRepository;
import com.hanghae.todoli.item.Dto.ExistItemListDto;
import com.hanghae.todoli.item.Dto.ItemRequestDto;
import com.hanghae.todoli.item.Dto.ItemResponseDto;
import com.hanghae.todoli.member.Member;
import com.hanghae.todoli.member.MemberRepository;
import com.hanghae.todoli.security.UserDetailsImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@Transactional
@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private InventoryRepository inventoryRepository;
    @Mock
    private CharacterRepository characterRepository;
    @Mock
    private EquipItemRepository equipItemRepository;
    @Mock
    private MemberRepository memberRepository;
    ItemService itemService;

    List<Inventory> inventories1 = new ArrayList<>();

    EquipItem equipItem1 = new EquipItem();
    Character character1 = new Character(
            1L, new CharacterImg().getCharImg(),
            100, 0, 100, 0, 1, 10000, inventories1, equipItem1);


    Member existMember = new Member(
            "test1@naver.com", "test1",
            "password1", false, character1);

    /*
   아이템 3개 생성
    */
    Item item1 = new Item(1L, "blackHair", Category.HAIR,
            "https://twodo-li.s3.ap-northeast-2.amazonaws.com/items/testhair_equip.png",
            "https://twodo-li.s3.ap-northeast-2.amazonaws.com/items/testhair_view.png",
            "https://twodo-li.s3.ap-northeast-2.amazonaws.com/items/testhair_thumb.png",
            1000
    );
    Item item2 = new Item(2L, "blackCloth", Category.CLOTH,
            "https://twodo-li.s3.ap-northeast-2.amazonaws.com/items/testcloth_equip.png",
            "https://twodo-li.s3.ap-northeast-2.amazonaws.com/items/testcloth_view.png",
            "\"https://twodo-li.s3.ap-northeast-2.amazonaws.com/items/testcloth_thumb.png",
            2000);
    Item item3 = new Item(3L, "Ring", Category.ACCESSORY,
            "https://twodo-li.s3.ap-northeast-2.amazonaws.com/items/testAccessory_equip.png",
            "https://twodo-li.s3.ap-northeast-2.amazonaws.com/items/testAccessory_view.png",
            "https://twodo-li.s3.ap-northeast-2.amazonaws.com/items/testAccessory_thumb.png",
            3000);
    Item item4 = new Item(4L, "Ring2", Category.ACCESSORY,
            "https://twodo-li.s3.ap-northeast-2.amazonaws.com/items/testAccessory_equip.png",
            "https://twodo-li.s3.ap-northeast-2.amazonaws.com/items/testAccessory_view.png",
            "https://twodo-li.s3.ap-northeast-2.amazonaws.com/items/testAccessory_thumb.png",
            4000);

    UserDetailsImpl userDetails = new UserDetailsImpl(existMember);

    @BeforeEach
    void beforeEach() {
        this.itemService = new ItemService(
                itemRepository,
                inventoryRepository,
                characterRepository,
                equipItemRepository,
                memberRepository
        );
        existMember.setId(1L);
    }


    @Nested
    @DisplayName("가지고 있는 아이템 조회")
    class getExistItemListTest {

        @Test
        @DisplayName("가지고 있는 아이템 조회 성공")
        void getExistItemList() {
            //given
            Long memberId = userDetails.getMember().getId();

            given(memberRepository.findById(memberId))
                    .willReturn(Optional.ofNullable(existMember));

            Inventory inventory1 = new Inventory(item1, character1);
            Inventory inventory2 = new Inventory(item2, character1);
            inventories1.add(inventory1);
            inventories1.add(inventory2);

            //when
            List<ExistItemListDto> result = itemService.getExistItemList(userDetails);
            //then
            Assertions.assertEquals(item1.getName(), result.get(0).getName());
            Assertions.assertEquals(item1.getId(), result.get(0).getItemId());
            Assertions.assertEquals(item2.getName(), result.get(1).getName());
            Assertions.assertEquals(item2.getId(), result.get(1).getItemId());
        }

        @Test
        @DisplayName("가지고 있는 아이템 조회 실패 - 로그인한 유저 정보 없음")
        void getExistItemListFail1() {
            //given
            //when
            CustomException exception = assertThrows(CustomException.class,
                    () -> itemService.getExistItemList(userDetails));
            //then
            Assertions.assertEquals("해당 유저 정보를 찾을 수 없습니다.", exception.getErrorCode().getMessage());
        }
    }

    @Nested
    @DisplayName("상점 아이템 목록 조회")
    class getShopItemListTest {

        @Test
        @DisplayName("상점 아이템 목록 조회 성공")
        void getShopItemList() {
            //given
            Long memberId = userDetails.getMember().getId();

            given(memberRepository.findById(memberId))
                    .willReturn(Optional.ofNullable(existMember));

            Inventory inventory1 = new Inventory(item1, character1);
            Inventory inventory2 = new Inventory(item2, character1);
            inventories1.add(inventory1);
            inventories1.add(inventory2);

            List<Item> allItem = new ArrayList<>();
            allItem.add(item1);
            allItem.add(item2);
            allItem.add(item3);
            allItem.add(item4);
            given(itemRepository.findAllExceptBasic()).willReturn(allItem);

            //when
            ItemResponseDto result = itemService.getShopItemList(userDetails);
            //then
            Assertions.assertEquals(item1.getId(), result.getItems().get(0).getItemId());
            Assertions.assertEquals(allItem.size(), result.getItems().size());
            Assertions.assertEquals(userDetails.getMember().getCharacter().getInventory().get(0).getItem().getId(),
                    result.getInventories().get(0).getItemId());
            Assertions.assertEquals(userDetails.getMember().getCharacter().getInventory().size(),
                    result.getInventories().size());
        }

        @Test
        @DisplayName("상점 아이템 목록 조회 실패 - 로그인 유저 정보 없음")
        void getShopItemListFail1() {
            //given

            //when
            CustomException exception = assertThrows(CustomException.class,
                    () -> itemService.getShopItemList(userDetails));
            //then
            Assertions.assertEquals("해당 유저 정보를 찾을 수 없습니다.",
                    exception.getErrorCode().getMessage());
        }
    }


    @Nested
    @DisplayName("아이템 구매")
    class buyItemTest {

        @Test
        @DisplayName("아이템 구매 성공")
        void buyItem() {
            //given
            Long itemId = item1.getId();
            given(itemRepository.findById(itemId))
                    .willReturn(Optional.ofNullable(item1));
            given(memberRepository.findById(userDetails.getMember().getId()))
                    .willReturn(Optional.ofNullable(existMember));

            //when
            itemService.buyItem(itemId, userDetails);
            //then
            Assertions.assertEquals(9000, userDetails.getMember().getCharacter().getMoney());
        }

        @Test
        @DisplayName("아이템 구매 실패 - 구매 할 아이템이 없는 경우")
        void buyItemFail1() {
            //given
            Long itemId = item1.getId();

            //when
            CustomException exception = assertThrows(CustomException.class,
                    () -> itemService.buyItem(itemId, userDetails));
            //then
            Assertions.assertEquals("아이템이 존재하지 않습니다.", exception.getErrorCode().getMessage());
        }

        @Test
        @DisplayName("아이템 구매 실패 - 로그인 유저 정보 없는 경우")
        void buyItemFail2() {
            //given
            Long itemId = item1.getId();
            given(itemRepository.findById(itemId))
                    .willReturn(Optional.ofNullable(item1));

            //when
            CustomException exception = assertThrows(CustomException.class,
                    () -> itemService.buyItem(itemId, userDetails));
            //then
            Assertions.assertEquals("해당 유저 정보를 찾을 수 없습니다.", exception.getErrorCode().getMessage());
        }

        @Test
        @DisplayName("아이템 구매 실패 - 아이템 중복 구매")
        void buyItemFail3() {
            //given
            Long itemId = item1.getId();
            given(itemRepository.findById(itemId))
                    .willReturn(Optional.ofNullable(item1));
            given(memberRepository.findById(userDetails.getMember().getId()))
                    .willReturn(Optional.ofNullable(existMember));

            Inventory inventory1 = new Inventory(item1, character1);
            inventories1.add(inventory1);

            given(inventoryRepository.findByCharacterAndItem(userDetails.getMember().getCharacter(), item1))
                    .willReturn(Optional.of(inventory1));
            //when
            CustomException exception = assertThrows(CustomException.class,
                    () -> itemService.buyItem(itemId, userDetails));
            //then
            Assertions.assertEquals("이미 구매하신 물품입니다.", exception.getErrorCode().getMessage());
        }

        @Test
        @DisplayName("아이템 구매 실패 - 아이템 살 돈 없는 경우")
        void buyItemFail4() {
            //given
            Item item = new Item(
                    4L,
                    "testItem",
                    Category.ACCESSORY,
                    "equipImg",
                    "viewImg",
                    "thumbImg",
                    20000
            );

            Long itemId = item.getId();


            given(itemRepository.findById(itemId))
                    .willReturn(Optional.of(item));
            given(memberRepository.findById(userDetails.getMember().getId()))
                    .willReturn(Optional.ofNullable(existMember));

            //when
            CustomException exception = assertThrows(CustomException.class,
                    () -> itemService.buyItem(itemId, userDetails));
            //then
            Assertions.assertEquals("금액이 부족합니다.", exception.getErrorCode().getMessage());
        }

    }


    @Nested
    @DisplayName("아이템 장착")
    class equipItemTest {

        @Test
        @DisplayName("아이템 장착 성공")
        void equipItem() {

            //given
            Long itemId = item1.getId();

            //category : hair
            Inventory inventory1 = new Inventory(item1, character1);
            inventories1.add(inventory1);

            given(memberRepository.findById(userDetails.getMember().getId()))
                    .willReturn(Optional.ofNullable(existMember));
            given(itemRepository.findById(itemId))
                    .willReturn(Optional.ofNullable(item1));
            given(inventoryRepository.findByCharacterAndItem(userDetails.getMember().getCharacter(),
                    item1))
                    .willReturn(Optional.of(inventory1));

            //when
            EquipItemDto result = itemService.equipItem(itemId, userDetails);
            //then
            Assertions.assertEquals(userDetails.getMember().getCharacter().getEquipItem().getHairId()
                    , result.getItemId());
        }

        @Test
        @DisplayName("아이템 장착 실패 - 로그인 유저 정보 없는 경우")
        void equipItemFail1() {

            //given
            Long itemId = item1.getId();

            //when
            CustomException exception = assertThrows(CustomException.class,
                    () -> itemService.equipItem(itemId, userDetails));
            //then
            Assertions.assertEquals("해당 유저 정보를 찾을 수 없습니다.",
                    exception.getErrorCode().getMessage());
        }

        @Test
        @DisplayName("아이템 장착 실패 - 아이템 없는 경우")
        void equipItemFail2() {

            //given
            Long itemId = item1.getId();

            given(memberRepository.findById(userDetails.getMember().getId()))
                    .willReturn(Optional.ofNullable(existMember));

            //when
            CustomException exception = assertThrows(CustomException.class,
                    () -> itemService.equipItem(itemId, userDetails));
            //then
            Assertions.assertEquals("아이템이 존재하지 않습니다.",
                    exception.getErrorCode().getMessage());
        }

        @Test
        @DisplayName("아이템 장착 실패 - 인벤토리에 아이템 없는 경우")
        void equipItemFail3() {

            //given
            Long itemId = item1.getId();

            given(memberRepository.findById(userDetails.getMember().getId()))
                    .willReturn(Optional.ofNullable(existMember));
            given(itemRepository.findById(itemId))
                    .willReturn(Optional.ofNullable(item1));

            //when
            CustomException exception = assertThrows(CustomException.class,
                    () -> itemService.equipItem(itemId, userDetails));
            //then
            Assertions.assertEquals("아이템을 먼저 구매해 주세요.",
                    exception.getErrorCode().getMessage());
        }
    }

    @Nested
    @DisplayName("아이템 등록")
    class inputItemTest {

        @Test
        @DisplayName("아이템 등록 성공")
        void inputItem() {

            //given
            ItemRequestDto itemRequestDto = new ItemRequestDto(
                    "testItem",
                    Category.ACCESSORY,
                    "equipImg",
                    "viewImg",
                    "thumbImg",
                    2000
            );

            //when
            itemService.inputItem(itemRequestDto);

            //then
            verify(itemRepository, times(1)).save(any(Item.class));
        }
    }
}