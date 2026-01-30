package santaOps.santaLog.config;

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
import santaOps.santaLog.repository.redis.RefreshTokenRepository;
import santaOps.santaLog.service.UserService;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity // 시큐리티 설정을 활성화합니다.
public class WebOAuthSecurityConfig {

    private final OAuth2UserCustomService oAuth2UserCustomService;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserService userService;
    private final RedisTemplate<String, Object> redisTemplate;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // 1. 기본 보안 설정 (CSRF 및 FrameOptions 비활성화)
        http
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers.frameOptions(frame -> frame.disable()));

        // 2. HTTP 기본 인증 및 폼 로그인 비활성화 (JWT 사용 환경)
        http
                .httpBasic(httpBasic -> httpBasic.disable())
                .formLogin(formLogin -> formLogin.disable())
                .logout(logout -> logout.disable()); // 로그아웃 로직은 컨트롤러에서 처리하도록 비활성화

        // 3. 세션 사용 안 함 (STATELESS)
        http
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // 4. JWT 인증 필터 추가
        http
                .addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        // 5. URL별 권한 설정
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/token", "/login", "/signup", "/logout").permitAll()
                        .requestMatchers("/auth/admin/login", "/admin/login").permitAll()
                        .requestMatchers("/admin/**", "/api/admin/users/count").hasRole("ADMIN")
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().permitAll()
                );

        // 6. OAuth2 로그인 설정 (가장 중요!)
        http.oauth2Login(oauth2 -> oauth2
                .loginPage("/login")
                .authorizationEndpoint(authorization -> authorization
                        .baseUri("/oauth2/authorization") // 이 주소로 구글 로그인을 시작합니다.
                        .authorizationRequestRepository(oAuth2AuthorizationRequestBasedOnCookieRepository()))
                .redirectionEndpoint(redirection -> redirection
                        .baseUri("/login/oauth2/code/*")) // 구글이 코드를 보낼 주소
                .userInfoEndpoint(userInfo -> userInfo.userService(oAuth2UserCustomService))
                .successHandler(oAuth2SuccessHandler()) // 성공 시 실행될 핸들러
        );

        return http.build();
    }

    // SuccessHandler 빈 등록
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

    // Stateless 환경에서 OAuth2 정보를 저장할 쿠키 기반 레포지토리
    @Bean
    public OAuth2AuthorizationRequestBasedOnCookieRepository oAuth2AuthorizationRequestBasedOnCookieRepository() {
        return new OAuth2AuthorizationRequestBasedOnCookieRepository();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}