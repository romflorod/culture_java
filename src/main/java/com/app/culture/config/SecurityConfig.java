package com.app.culture.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
            .requestMatchers("/", "/login", "/register", "/h2-console/**", 
            "/css/**", "/js/**", "/searchanime", "/api/animes/**", "/markAnime", "/unmarkAnime",
            "/myanimes", "/searchanime", "/animedetails/**", "/searchmovie", "/moviedetails/**", 
            "/markMovie", "/unmarkMovie", "/mymovies","/generateAnimeRecommendations","/generateAnimeRecommendations",
            "/searchbook", "/markBook", "/unmarkBook", "/mybooks", "/bookdetails/**",
            "/searchmanga", "/markManga", "/unmarkManga", "/mymangas","/mangadetails/**").permitAll()
                    )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/")
                .permitAll()
                .disable()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            )
            .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()));
        
        return http.build();
    }
}