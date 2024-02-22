package com.shopClone.config;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {
    // 구글링 결과PrincipalOauth2UserService extends DefaultOAuth2UserService을, loaduser을 구현해야한다 
    // 구글로부터 받은 userRequest 데이터에 대한 후처리가 되는 함수
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
        // 회원가입을 강제로 진행해볼 예점
        return super.loadUser(userRequest);
    }

}
