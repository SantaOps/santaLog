package santaOps.santaLog.config.oauth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import santaOps.santaLog.config.jwt.TokenProvider;
import santaOps.santaLog.domain.RefreshToken;
import santaOps.santaLog.domain.User;
import santaOps.santaLog.repository.RefreshTokenRepository;
import santaOps.santaLog.service.UserService;
import santaOps.santaLog.util.CookieUtil;

import java.io.IOException;
import java.time.Duration;

@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    public static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";
    public static final String ACCESS_TOKEN_COOKIE_NAME = "ACCESS_TOKEN";
    public static final Duration REFRESH_TOKEN_DURATION = Duration.ofDays(14);
    public static final Duration ACCESS_TOKEN_DURATION = Duration.ofDays(1);

    // Article 서버(8081)의 게시글 목록 주소로 지정
    public static final String REDIRECT_PATH = "http://localhost:8081/articles";

    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final OAuth2AuthorizationRequestBasedOnCookieRepository authorizationRequestRepository;
    private final UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        User user = userService.findByEmail((String) oAuth2User.getAttributes().get("email"));

        // 1. 리프레시 토큰 생성 및 저장 -> 쿠키에 저장
        String refreshToken = tokenProvider.generateToken(user, REFRESH_TOKEN_DURATION);
        saveRefreshToken(user.getId(), user.getUsername(),refreshToken);
        addRefreshTokenToCookie(request, response, refreshToken);

        // 2. 액세스 토큰 생성 -> 쿠키에 저장
        // 도메인(localhost)이 같으면 8080에서 구운 쿠키를 8081로 전송
        String accessToken = tokenProvider.generateToken(user, ACCESS_TOKEN_DURATION);
        addAccessTokenToCookie(response, accessToken);

        // 3. 리다이렉트 경로 설정 (http://localhost:8081/articles)
        String targetUrl = getTargetUrl();

        // 인증 관련 설정값들 정리
        clearAuthenticationAttributes(request, response);

        // 4. 브라우저를 Article 서버(8081)로 이동시킴
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    private void saveRefreshToken(Long userId, String username, String newRefreshToken) {
        RefreshToken refreshToken = refreshTokenRepository.findById(userId)
                .map(entity -> entity.update(newRefreshToken, username)) // 업데이트 시 이름도 갱신
                .orElse(new RefreshToken(userId, newRefreshToken, username)); // 새로 저장

        refreshTokenRepository.save(refreshToken);
    }

    private void addRefreshTokenToCookie(HttpServletRequest request, HttpServletResponse response, String refreshToken) {
        int cookieMaxAge = (int) REFRESH_TOKEN_DURATION.toSeconds();
        CookieUtil.deleteCookie(request, response, REFRESH_TOKEN_COOKIE_NAME);
        CookieUtil.addCookie(response, REFRESH_TOKEN_COOKIE_NAME, refreshToken, cookieMaxAge);
    }

    private void addAccessTokenToCookie(HttpServletResponse response, String accessToken) {
        int cookieMaxAge = (int) ACCESS_TOKEN_DURATION.toSeconds();
        CookieUtil.addCookie(response, ACCESS_TOKEN_COOKIE_NAME, accessToken, cookieMaxAge);
    }

    private void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        authorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }

    private String getTargetUrl() {
        return UriComponentsBuilder.fromUriString(REDIRECT_PATH)
                .build()
                .toUriString();
    }
}