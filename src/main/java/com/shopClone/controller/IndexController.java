package com.shopClone.controller;

import com.shopClone.auth.PrincipalDetails;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class IndexController {

    @GetMapping("/test/login")
    public @ResponseBody String loginTest(Authentication authentication,
                                          @AuthenticationPrincipal PrincipalDetails userDetails) { // DI(의존성주입)
        System.out.println("/test/login =========");
        // 1. Authetication을 PrincipalDetails로 캐스팅하여 사용하는 방법
        // PrincipalDetails는 우리가 앞서 생성했던 config.auth.PrincipalDetails
    // PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
//       System.out.println("principalDetails = " + principalDetails.());

        // 2. @AuthenticationPrincipal을 사용하는 방법
        // PrincipalDetails가 UserDetails를 상속받고 있기 때문에
        // UserDetails 대신에 PrincipalDetails를 사용할 수 있음
        System.out.println("userDetails = " + userDetails.getUsername());
        return null;
    }

    @GetMapping("/test/oauth/login")
    public String loginOAuthTest(Authentication authentication,
                                 @AuthenticationPrincipal OAuth2User oAuth) { // DI(의존성주입)
        System.out.println("/test/oauth/login =========");
        // 1. 캐스팅해서 가져오기
//        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
//        System.out.println("authentication = " + oAuth2User.getAttributes());
        // 2. @AuthenticationPrincipal로 가져오기
        System.out.println("OAuth2User = " + oAuth.getAttributes());
        return "OAuth 세션 정보 확인하기";
    }
}
