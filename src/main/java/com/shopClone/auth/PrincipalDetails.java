package com.shopClone.auth;

import com.shopClone.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;


public class PrincipalDetails implements UserDetails, OAuth2User {

    private Member member;
    protected PrincipalDetails(Member member){
        this.member=member;
    }

    private Map<String, Object> attributes;

    public PrincipalDetails(Member member, Map<String, Object> attributes) {
        this.member = member;
        this.attributes = attributes;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection =new ArrayList<>();
//        String role = (String) attributes.get("role");
//        System.out.println("권한정보 테스트"+role);

        collection.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                System.out.println("제대로 롤을 반환하는지 test2 "+member.getRole().toString());
                return member.getRole().toString(); }
        });

        collection.add(new SimpleGrantedAuthority("ROLE_" + member.getRole().toString()));
        return collection;
    }

    @Override
    public String getPassword() {
        return member.getPassword();
    }

    @Override
    public String getUsername() {
        return member.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
