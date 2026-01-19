package santaOps.santaLog.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import santaOps.santaLog.config.jwt.TokenProvider;
import santaOps.santaLog.dto.AddUserRequest;
import santaOps.santaLog.service.RefreshTokenService;
import santaOps.santaLog.service.UserService;
import jakarta.servlet.http.Cookie;
import santaOps.santaLog.util.CookieUtil;

@RequiredArgsConstructor
@Controller
public class UserApiController {

    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final TokenProvider tokenProvider;

    @PostMapping("/user")
    public String signup(AddUserRequest request, RedirectAttributes redirectAttributes) {
        try {
            userService.save(request);
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/signup";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response){

        // 3. 쿠키에서 ACCESS_TOKEN 꺼내기
        String accessToken = CookieUtil.getCookie(request, "ACCESS_TOKEN")
                .map(Cookie::getValue)
                .orElse(null);

        if (accessToken != null) {
            try {
                // 4. 토큰에서 userId 추출
                Long userId = tokenProvider.getUserId(accessToken);

                // 5. Redis 데이터 삭제
                refreshTokenService.deleteByUserId(userId);
            } catch (Exception e) {
                System.out.println(">>> 토큰 분석 실패 OR Redis 삭제 오류: " + e.getMessage());
            }
        }

        // 6. 브라우저 쿠키 삭제
        Cookie accessTokenCookie = new Cookie("ACCESS_TOKEN", null);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(0);
        response.addCookie(accessTokenCookie);

        Cookie refreshCookie = new Cookie("refresh_token", null);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(0);
        response.addCookie(refreshCookie);

        return "redirect:http://localhost:8080/login";
    }

    @GetMapping("/users/count")
    public ResponseEntity<Long> getUserCount() {
        return ResponseEntity.ok(userService.countUsers());
    }

}
