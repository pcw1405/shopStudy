package com.shopClone.config;

import com.shopClone.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    MemberService memberService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http.formLogin()
                .loginPage("/members/login") //로그인 페이지 url 설정
                .defaultSuccessUrl("/") // 성공시 이동할 url
                .usernameParameter("email") //로그인시 사용할 피라미터 이름으로 eamil 지정
                .failureUrl("/members/login/error")
                .and()
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/members/logout"))
                .logoutSuccessUrl("/") //로그아웃 성공시 이동할 url

        ;
        http.authorizeRequests()  // 시큐리티 처리에 httpServletRequest를 이용한다는 의미
                .mvcMatchers("/css/**","/js/**","/img/**").permitAll()
                .antMatchers("/favicon.ico").permitAll()  // Allow access to favicon.ico
                .mvcMatchers("/","/members/**","/item/**","/images/**").permitAll()
                .mvcMatchers("/admin/**").hasRole("ADMIN") // ADMIN role일 경우 접근 가능
                .anyRequest().authenticated()
        ;

        http.exceptionHandling()
                .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
        ;

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
        //PasswordEncoder 인터페이스 구현체 BCryptPasswordEncoder();
    }

}
