package com.ttasum.memorial.config;

import com.ttasum.memorial.domain.repository.admin.AdminEmployeeRepository;
import com.ttasum.memorial.service.admin.AdminService;
import com.ttasum.memorial.service.admin.AdminServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
//    private final AdminEmployeeRepository adminEmployeeRepository;

    // 스프링 시큐리티 비활성화
    @Bean
    public WebSecurityCustomizer configure() {
        return web -> web.ignoring()
                .requestMatchers(
                        new AntPathRequestMatcher("/h2-console/**"),
                        new AntPathRequestMatcher("/static/**"),
                        new AntPathRequestMatcher("/css/**"),
                        new AntPathRequestMatcher("/images/**"),
                        new AntPathRequestMatcher("/js/**"));
    }

    // 특정 HTTP 요청에 대한 웹 기반 보안 구성
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                new AntPathRequestMatcher("/admin/login"),
                                new AntPathRequestMatcher("/admin/signup"),
                                new AntPathRequestMatcher("/admin/checkId"),
                                new AntPathRequestMatcher("/"),
                                new AntPathRequestMatcher("/donationLetters/**"),
                                new AntPathRequestMatcher("/heavenLetters/**"),
                                new AntPathRequestMatcher("/notices/**"),
                                new AntPathRequestMatcher("/remembrance/**"),
                                new AntPathRequestMatcher("/admin/flowers/**"),
                                new AntPathRequestMatcher("/recipientLetters/**")
                        )
                        .permitAll() // 익명 접근 허용
                        .requestMatchers(
                                new AntPathRequestMatcher("/admin/blameTextList"),
                                new AntPathRequestMatcher("/admin/blameText/story"),
                                new AntPathRequestMatcher("/admin/blameText/story/detail"),
                                new AntPathRequestMatcher("/admin/blameText/story/**"),
                                new AntPathRequestMatcher("/admin/blameText/comment")
                        )
                        .hasRole("ADMIN")  //hasRole("ADMIN")은 내부적으로 ROLE_ADMIN으로 처리
                        .anyRequest()
                        .authenticated()
                ).exceptionHandling(exception -> exception
                        .accessDeniedPage("/admin/noAuthorization"))
                .formLogin(formLogin -> formLogin
                        .loginPage("/admin/login")
                        .defaultSuccessUrl("/admin/dashboard", true))
                .logout(logout -> logout
                        .logoutSuccessUrl("/admin/login")
                        .invalidateHttpSession(true))
                .csrf(AbstractHttpConfigurer::disable)
                .build();
    }

    // 인증 관리자 관련 설정
    @Bean
    public AuthenticationManager authenticationManagerBean(
            HttpSecurity http,
            BCryptPasswordEncoder bCryptPasswordEncoder,
            AdminServiceImpl userDetailsService) throws Exception {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(bCryptPasswordEncoder);
        return new ProviderManager(provider);
    }

    // 비밀번호 암호화
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
