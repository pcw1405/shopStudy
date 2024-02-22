package com.shopClone.auth;

import com.shopClone.entity.Member;
import com.shopClone.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class PrincipalDetailsService implements UserDetailsService  {

    @Autowired
    private MemberRepository memberRepository;
    public PrincipalDetailsService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }
//    @Autowired
//    private PrincipalDetails principalDetails;



    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
        System.out.println("username = " + username);
        Member memberEntity =memberRepository.findByEmail(username);

        if(memberEntity !=null){
            User.builder()
                    .username(memberEntity.getEmail())
                    .password(memberEntity.getPassword())
                    .roles(memberEntity.getRole().toString())
                    //배열로 반환되어 문자열로 변환해서 넣어줘야함
                    .build();
            return new PrincipalDetails(memberEntity);

        }
        return null;
    }
}
