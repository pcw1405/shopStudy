package com.shopClone.controller;

import com.shopClone.auth.PrincipalDetails;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

public class IndexController {

//  @GetMapping("/test")
//    public @ResponseBody String test(Authentication authentication,
//                                          @AuthenticationPrincipal PrincipalDetails details) {
//        System.out.println("/test/login =========");
//
//      UserDetails authenticationDetail=(UserDetails) authentication.get();
//
//      System.out.println("id text pricipalDetails: "+details.getUsername());
//      System.out.println("id text (userDetails)Authentication : "+authenticationDetail.getUsername());
//
//   }
}
