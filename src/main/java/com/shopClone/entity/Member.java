package com.shopClone.entity;

import com.shopClone.constant.Role;
import com.shopClone.dto.MemberFormDto;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.security.Timestamp;

@Entity
@Table(name="member")
@Getter
@Setter
@ToString
public class Member  extends BaseEntity{
    @Id
    @Column(name="member_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    @Column(unique = true) //중복값 허용안함
    private String email;
    private String password;
    private String address;
    @Enumerated(EnumType.STRING)
    private Role role;


    // provider / providerId 추가
    private String provider; // 값을 받아온 도메인('google')
    private String providerId; // 도메인에서 사용하는 id('sub값')

    private Timestamp loginDate; // 로그인 한 날짜
    public static Member createMember(MemberFormDto memberFormDto,
                                      PasswordEncoder passwordEncoder){
        Member member = new Member();
        member.setName(memberFormDto.getName());
        member.setEmail(memberFormDto.getEmail());
        member.setAddress(memberFormDto.getAddress());
        String password = passwordEncoder.encode(memberFormDto.getPassword());
        member.setPassword(password);
        member.setRole(Role.ADMIN);
        return member;
    }
    //비밀번호 암호화



}