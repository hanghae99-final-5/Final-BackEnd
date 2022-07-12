package com.hanghae.todoli.googleLogin;

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
import com.hanghae.todoli.member.Member;
import com.hanghae.todoli.member.MemberRepository;
import com.hanghae.todoli.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OAuthService {
    private final GoogleOauth googleOauth;
    private final HttpServletResponse response;
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final CharacterRepository characterRepository;
    private final EquipItemRepository equipItemRepository;
    private final ItemRepository itemRepository;
    private final InventoryRepository inventoryRepository;

    public void request(SocialLoginType socialLoginType) throws IOException {
        String redirectURL;
        switch (socialLoginType) {
            case GOOGLE: {
                //각 소셜 로그인을 요청하면 소셜로그인 페이지로 리다이렉트 해주는 프로세스이다.
                redirectURL = googleOauth.getOauthRedirectURL();
            }
            break;
            default: {
                throw new IllegalArgumentException("알 수 없는 소셜 로그인 형식입니다.");
            }
        }
        response.sendRedirect(redirectURL);
    }

    public GetSocialOAuthRes oAuthLogin(SocialLoginType socialLoginType, String code) throws IOException {

        switch (socialLoginType) {
            case GOOGLE: {
                //구글로 일회성 코드를 보내 액세스 토큰이 담긴 응답객체를 받아옴
                ResponseEntity<String> accessTokenResponse = googleOauth.requestAccessToken(code);
                //응답 객체가 JSON형식으로 되어 있으므로, 이를 deserialization해서 자바 객체에 담을 것이다.
                GoogleOAuthToken oAuthToken = googleOauth.getAccessToken(accessTokenResponse);

                //액세스 토큰을 다시 구글로 보내 구글에 저장된 사용자 정보가 담긴 응답 객체를 받아온다.
                ResponseEntity<String> userInfoResponse = googleOauth.requestUserInfo(oAuthToken);
                //다시 JSON 형식의 응답 객체를 자바 객체로 역직렬화한다.
                GoogleUser googleUser = googleOauth.getUserInfo(userInfoResponse);

                //우리 서버의 db와 대조하여 해당 user가 존재하는 지 확인한다.
                String username = googleUser.getEmail();
                if (memberRepository.findByUsername(username).isEmpty()) {
                    String nickname = googleUser.getName();
                    String password = UUID.randomUUID().toString();

                    //멤버생성과 동시에 캐릭터, 장착아이템 같이 생성
                    Item basicAccessory = itemRepository.findById(1L).orElseThrow(
                            () -> new CustomException(ErrorCode.NO_ITEM)
                    );
                    Item basicHair = itemRepository.findById(2L).orElseThrow(
                            () -> new CustomException(ErrorCode.NO_ITEM)
                    );
                    Item basicCloth = itemRepository.findById(3L).orElseThrow(
                            () -> new CustomException(ErrorCode.NO_ITEM)
                    );
                    EquipItem equipItem = new EquipItem(1L, 2L, 3L);
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

                if (username != null) {
                    Member member = memberRepository.findByUsername(username).orElseThrow(
                            () -> new CustomException(ErrorCode.NOT_FOUND_MEMBER));
                    //서버에 user가 존재하면 앞으로 회원 인가 처리를 위한 jwtToken을 발급한다.
                    String Authorization = jwtTokenProvider.createToken(username, member.getNickname());
                    //액세스 토큰과 Authorization, 이외 정보들이 담긴 자바 객체를 다시 전송한다.
                    return new GetSocialOAuthRes(Authorization, username, oAuthToken.getAccess_token(), oAuthToken.getToken_type());
                } else {
                    throw new IllegalArgumentException("계정이 존재하지 않습니다.");
                }
            }
            default: {
                throw new IllegalArgumentException("알 수 없는 소셜 로그인 형식입니다.");
            }
        }
    }
}