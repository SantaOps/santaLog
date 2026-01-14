package santaOps.santaLog.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import santaOps.santaLog.config.jwt.TokenProvider;
import santaOps.santaLog.config.oauth.OAuth2SuccessHandler;
import santaOps.santaLog.domain.Role;
import santaOps.santaLog.domain.User;
import santaOps.santaLog.service.UserService;

import java.time.Duration;

@Controller
@RequiredArgsConstructor
public class AdminAuthController {

    private final UserService userService;
    private final TokenProvider tokenProvider;



    @PostMapping("/auth/admin/login")
    public String adminLogin(
            @RequestParam String email,
            @RequestParam String password,
            HttpServletResponse response
    ) {
        // ✅ 이전 SecurityContext 초기화
        SecurityContextHolder.clearContext();

        // ✅ 기존 ACCESS_TOKEN 쿠키 삭제
        Cookie oldCookie = new Cookie("ACCESS_TOKEN", null);
        oldCookie.setPath("/");
        oldCookie.setMaxAge(0);
        response.addCookie(oldCookie);

        User admin = userService.authenticate(email, password);

        if (admin.getRole() != Role.ADMIN) {
            return "redirect:/auth/admin/login?error=not_admin";
        }

        // 새 JWT 발급
        String token = tokenProvider.generateToken(admin, OAuth2SuccessHandler.ACCESS_TOKEN_DURATION);

        // HttpOnly 쿠키로 내려주기
        Cookie cookie = new Cookie("ACCESS_TOKEN", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge((int) OAuth2SuccessHandler.ACCESS_TOKEN_DURATION.toSeconds());
        cookie.setSecure(false); // TODO: HTTPS 사용 시 true로 변경
        response.addCookie(cookie);

        // SecurityContext 반영
        SecurityContextHolder.getContext().setAuthentication(tokenProvider.getAuthentication(token));

        return "redirect:/articles";
    }
}
