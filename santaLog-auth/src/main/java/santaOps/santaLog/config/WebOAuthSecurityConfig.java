package santaOps.santaLog.config;

import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.client.RestTemplate;
import santaOps.santaLog.config.jwt.TokenProvider;
import santaOps.santaLog.config.oauth.OAuth2AuthorizationRequestBasedOnCookieRepository;
import santaOps.santaLog.config.oauth.OAuth2SuccessHandler;
import santaOps.santaLog.config.oauth.OAuth2UserCustomService;
import santaOps.santaLog.domain.User;
import santaOps.santaLog.repository.redis.RefreshTokenRepository;
import santaOps.santaLog.service.UserService;

import java.time.Duration;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class WebOAuthSecurityConfig {

    private final OAuth2UserCustomService oAuth2UserCustomService;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserService userService;
    private final RedisTemplate<String, Object> redisTemplate;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // 1. 기본 보안 설정
        http
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers.frameOptions(frame -> frame.disable()));

        // 2. 세션 정책 설정 (JWT 사용을 위해 STATELESS 유지)
        http
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // 3. JWT 인증 필터 추가
        http
                .addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        // 4. URL별 권한 설정
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/token", "/login", "/signup", "/logout").permitAll()
                        .requestMatchers("/auth/admin/login", "/admin/login").permitAll()
                        .requestMatchers("/admin/**", "/api/admin/users/count").hasRole("ADMIN")
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().permitAll()
                );

        // 5. 관리자 폼 로그인 설정 (포트 유실 해결 핵심)
        http
                .formLogin(form -> form
                        .loginPage("/admin/login")
                        .loginProcessingUrl("/auth/admin/login")
                        .successHandler((request, response, authentication) -> {
                            // [핵심] 로그인 성공 시 JWT 토큰 생성 및 쿠키 주입
                            User user = (User) authentication.getPrincipal();
                            String accessToken = tokenProvider.generateToken(user, Duration.ofDays(1));

                            Cookie cookie = new Cookie("ACCESS_TOKEN", accessToken);
                            cookie.setPath("/");
                            cookie.setHttpOnly(true); // 보안 설정
                            response.addCookie(cookie);

                            // [핵심] 포트 번호를 포함한 전체 경로로 리다이렉트
                            response.sendRedirect("https://santalog.cloud:31443/articles");
                        })
                        .failureUrl("/admin/login?error=id")
                )
                .httpBasic(httpBasic -> httpBasic.disable())
                .logout(logout -> logout.logoutSuccessUrl("/login"));

        // 6. OAuth2 로그인 설정
        http.oauth2Login(oauth2 -> oauth2
                .loginPage("/login")
                .authorizationEndpoint(authorization -> authorization
                        .baseUri("/oauth2/authorization")
                        .authorizationRequestRepository(oAuth2AuthorizationRequestBasedOnCookieRepository()))
                .redirectionEndpoint(redirection -> redirection
                        .baseUri("/login/oauth2/code/*"))
                .userInfoEndpoint(userInfo -> userInfo.userService(oAuth2UserCustomService))
                .successHandler(oAuth2SuccessHandler())
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

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}