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
        SecurityContextHolder.clearContext();

        User admin;
        try {
            admin = userService.authenticate(email, password);
        } catch (IllegalArgumentException e) {
            if (e.getMessage().equals("PW_NOT_MATCH")) {
                userService.updateAdminPassword(email, password);
                admin = userService.authenticate(email, password);
            } else {
                throw e;
            }
        }

        if (admin.getRole() != Role.ADMIN) {
            return "redirect:/admin/login?error=not_admin";
        }

        // 2. JWT 발급 및 쿠키 저장
        String token = tokenProvider.generateToken(admin, OAuth2SuccessHandler.ACCESS_TOKEN_DURATION);
        Cookie cookie = new Cookie("ACCESS_TOKEN", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge((int) OAuth2SuccessHandler.ACCESS_TOKEN_DURATION.toSeconds());
        response.addCookie(cookie);

        // 3. 현재 서버(8080) SecurityContext 반영
        SecurityContextHolder.getContext().setAuthentication(tokenProvider.getAuthentication(token));

        // 4. 게시글 서비스(8081)로 리다이렉트
        return "redirect:http://localhost:8081/articles";
    }


}