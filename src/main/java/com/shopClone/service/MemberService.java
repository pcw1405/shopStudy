package com.shopClone.service;

import com.shopClone.entity.Member;
import com.shopClone.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {
//        implements UserDetailsService
        private final MemberRepository memberRepository;

        public Member saveMember(Member member){
                validateDuplicateMember(member);
                return memberRepository.save(member);
        }

        private void validateDuplicateMember(Member member){
                Member findMember =memberRepository.findByEmail(member.getEmail());
                if(findMember !=null){
                        throw new IllegalStateException("이미 가입된 회원입니다");
                }
        }
        @Override
        public UserDetails loadUserByUsername(String email) throws
                UsernameNotFoundException{
                Member member =memberRepository.findByEmail(email);

                if(member==null){
                        throw new UsernameNotFoundException(email);
                }
                return User.builder()
                        .username(member.getEmail())
                        .password(member.getPassword())
                        .roles(member.getRole().toString())
                        //배열로 반환되어 문자열로 변환해서 넣어줘야함
                        .build();

        //userDetail을 overide해서 커스텀하는 방법 생각 필요
        }




}
