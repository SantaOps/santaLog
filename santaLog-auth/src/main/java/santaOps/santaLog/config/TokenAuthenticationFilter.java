package santaOps.santaLog.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import santaOps.santaLog.config.jwt.TokenProvider;

import java.io.IOException;
import java.util.Arrays;

@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;
    private final static String HEADER_AUTHORIZATION = "Authorization";
    private final static String TOKEN_PREFIX = "Bearer";

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        // 관리자 로그인 요청(POST) 등 인증이 새로 일어나는 곳은 기존 토큰 검증을 스킵
        // 이렇게 해야 이전 사용자의 토큰이 Context에 남는 문제를 방지
        String path = request.getRequestURI();
        return "/auth/admin/login".equals(path);
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        // 1. 헤더에서 토큰 가져오기
        String authorizationHeader = request.getHeader(HEADER_AUTHORIZATION);
        String token = getAccessToken(authorizationHeader);

        // 2. 헤더에 없을 경우 쿠키에서 토큰 가져오기
        if (token == null) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                token = Arrays.stream(cookies)
                        .filter(cookie -> "ACCESS_TOKEN".equals(cookie.getName()))
                        .findFirst()
                        .map(Cookie::getValue)
                        .orElse(null);
            }
        }

        // 토큰 디버깅 로그 추가
        if (token != null) {
            if (tokenProvider.validToken(token)) {
                System.out.println("✅ [Filter] 유효한 토큰 발견! 경로: " + path);
                Authentication authentication = tokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                System.out.println("❌ [Filter] 토큰이 만료됨! 인증 정보 삭제. 경로: " + path);
                SecurityContextHolder.clearContext();
            }
        } else {
            System.out.println("ℹ️ [Filter] 토큰이 없음 (익명 사용자). 경로: " + path);
            SecurityContextHolder.clearContext();
        }

        if (token != null && tokenProvider.validToken(token)) {
            Authentication authentication = tokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } else {
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    private String getAccessToken(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith(TOKEN_PREFIX)) {
            return authorizationHeader.substring(TOKEN_PREFIX.length()).trim();
        }
        return null;
    }
}