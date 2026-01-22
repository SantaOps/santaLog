package santaOps.santaLog.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import santaOps.santaLog.config.jwt.TokenProvider;
import santaOps.santaLog.config.oauth.OAuth2AuthorizationRequestBasedOnCookieRepository;
import santaOps.santaLog.config.oauth.OAuth2SuccessHandler;
import santaOps.santaLog.config.oauth.OAuth2UserCustomService;
import santaOps.santaLog.repository.redis.RefreshTokenRepository;
import santaOps.santaLog.service.RefreshTokenService;
import santaOps.santaLog.service.UserService;

@RequiredArgsConstructor
@Configuration
public class WebOAuthSecurityConfig {

    private final OAuth2UserCustomService oAuth2UserCustomService;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final RedisTemplate<String, Object> redisTemplate;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .headers(headers ->
                        headers.frameOptions(frame -> frame.disable())
                );

        http
                .httpBasic().disable()
                .formLogin().disable();


        http
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http
                .addFilterBefore(
                        tokenAuthenticationFilter(),
                        UsernamePasswordAuthenticationFilter.class
                );

        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/token", "/login", "/signup", "/logout").permitAll()
                        .requestMatchers("/auth/admin/login").permitAll()
                        .requestMatchers("/admin/login").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/**").authenticated()
                        .requestMatchers("/api/admin/users/count").hasRole("ADMIN")
                        .anyRequest().permitAll()
                );
        http.logout(logout -> logout.disable()); // 이걸 안 해주면 시큐리티가 가로챔

        http.oauth2Login(oauth2 -> oauth2
                .loginPage("/login")
                .authorizationEndpoint(authorization -> authorization
                        .authorizationRequestRepository(oAuth2AuthorizationRequestBasedOnCookieRepository()))
                .successHandler(oAuth2SuccessHandler())
                .userInfoEndpoint(userInfo -> userInfo
                        .userService(oAuth2UserCustomService))
        );
        return http.build();
    }


    @Bean
    public OAuth2SuccessHandler oAuth2SuccessHandler() {
        return new OAuth2SuccessHandler(
                tokenProvider,
                refreshTokenRepository,
                oAuth2AuthorizationRequestBasedOnCookieRepository(),
                userService,
                redisTemplate
        );
    }

    @Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter() {
        return new TokenAuthenticationFilter(tokenProvider);
    }

    @Bean
    public OAuth2AuthorizationRequestBasedOnCookieRepository oAuth2AuthorizationRequestBasedOnCookieRepository() {
        return new OAuth2AuthorizationRequestBasedOnCookieRepository();
    }
}