package com.shopClone.auth;

import com.shopClone.constant.Role;
import com.shopClone.entity.Member;
import com.shopClone.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {
    // 구글링 결과PrincipalOauth2UserService extends DefaultOAuth2UserService을, loaduser을 구현해야한다 
    // 구글로부터 받은 userRequest 데이터에 대한 후처리가 되는 함수

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;


    private Role convertToRole(String role) {
        // 예를 들어, "ADMIN" 문자열을 Role.ADMIN 열거형으로 변환하는 메서드
        return Role.valueOf(role);
    }
    @Autowired
    private MemberRepository memberRepository;
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        // registrationId로 어떤 oauth로 로그인 했는지 알 수 있음
        System.out.println("userRequest : " + userRequest.getClientRegistration());
        System.out.println("userRequest = " + userRequest.getAccessToken().getTokenValue());

        // 구글 로그인 버튼 클릭 -> 구글 로그인 창 -> 로그인을 완료
        // -> code를 리턴(Oauth-client라이브러리 -> AccessToken 요청

        // userRequest정보 -> loadUser함수 호출 -> 구글로부터 회원 프로필을 받아줌
        System.out.println("userRequest = " + super.loadUser(userRequest).getAttributes());

        OAuth2User oAuth2User = super.loadUser(userRequest);
      // 회원가입을 강제로 진행해볼 예정
        String provider = userRequest.getClientRegistration().getRegistrationId(); // google
        String providerId = oAuth2User.getAttribute("sub");
        String username = provider + "_" + providerId; // google_105156291955329144943
       String password = bCryptPasswordEncoder.encode("1111");
        String email = oAuth2User.getAttribute("email");
        String role = "ADMIN";
        Member memberEntity = memberRepository.findByEmail(email);

        // 회원 중복 체크
        if (memberEntity == null) {
            memberEntity = Member.builder()
                    .name(username)
                    .password(password)
                    .email(email)
                    .role(convertToRole(role))
                    .provider(provider)
                    .providerId(providerId)
                    .build();
            memberRepository.save(memberEntity);
        }

        return new PrincipalDetails(memberEntity, oAuth2User.getAttributes());
    }

}
