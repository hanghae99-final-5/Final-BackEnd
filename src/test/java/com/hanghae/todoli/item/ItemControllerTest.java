package com.hanghae.todoli.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanghae.todoli.item.Dto.ItemRequestDto;
import com.hanghae.todoli.member.Member;
import com.hanghae.todoli.security.MockSpringSecurityFilter;
import com.hanghae.todoli.security.SecurityConfig;
import com.hanghae.todoli.security.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.security.Principal;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class,
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = SecurityConfig.class
                )})
@MockBean(JpaMetamodelMappingContext.class)
class ItemControllerTest {

    @MockBean
    ItemService itemService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;
    private Principal mockPrincipal;

    private void mockUserSetup() {
        //mock 테스트 유저 생성
        String username = "테스트 유저";
        String nickname = "테스트";
        String password = "test123";
        Member testMember = new Member(username, nickname, password, false, null);
        UserDetailsImpl testUserDetail = new UserDetailsImpl(testMember);
        mockPrincipal = new UsernamePasswordAuthenticationToken(testUserDetail, "", null);
    }

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity(new MockSpringSecurityFilter()))
                .build();
    }

    @Test
    @DisplayName("가지고 있는 아이템 조회")
    void getExistItemList() throws Exception {
        this.mockUserSetup();

        mvc.perform(get("/api/inventories")
                        .principal(mockPrincipal))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("상점 아이템 목록 조회")
    void getShopItemList() throws Exception {
        this.mockUserSetup();

        mvc.perform(get("/api/items")
                        .principal(mockPrincipal))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("아이템 구매")
    void buyItem() throws Exception {
        this.mockUserSetup();

        mvc.perform(post("/api/items/{itemId}",
                        1)
                        .principal(mockPrincipal))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("아이템 장착")
    void equipItem() throws Exception {
        this.mockUserSetup();

        mvc.perform(patch("/api/items/{itemId}",
                        1)
                        .principal(mockPrincipal))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("아이템 등록")
    void inputItem() throws Exception {

        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .name("testItem")
                .category(Category.HAIR)
                .equipImg("testEquipImg")
                .viewImg("testViewImg")
                .thumbnailImg("testThumbnailImg")
                .price(100)
                .build();

        String content = objectMapper.writeValueAsString(itemRequestDto);

        mvc.perform(post("/api/items"
                        )
                                .content(content)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(print());
    }
}