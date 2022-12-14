package com.studyolle.configure;

import com.studyolle.account.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final AccountService accountService;
    private final DataSource dataSource;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrfConfig -> csrfConfig
                        .ignoringAntMatchers("/h2-console/**"))
                .authorizeRequests(urlRegistry -> urlRegistry
                        .mvcMatchers("/", "/login", "/sign-up", "/check-email-token", "/email-login",
                                "/check-email-login", "/login-link", "/login-by-email").permitAll()
                        .mvcMatchers(HttpMethod.GET, "/profile/*").permitAll()
                        .antMatchers("/h2-console/**").permitAll()
                        .anyRequest().authenticated())
                .headers(headersConfig -> headersConfig
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
                .formLogin(formLoginConfig -> formLoginConfig.loginPage("/login")
                        .permitAll())
                .logout(logoutConfig -> logoutConfig.logoutSuccessUrl("/"))
                .rememberMe(rememberMeConfig -> rememberMeConfig
                        .userDetailsService(accountService)
                        .tokenRepository(tokenRepository()));
        return http.build();
    }

    @Bean
    public PersistentTokenRepository tokenRepository() {
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dataSource);
        return jdbcTokenRepository;
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .mvcMatchers("/node_modules/**")
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }
}
