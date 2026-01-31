package santaOps.santaLog.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import santaOps.santaLog.config.jwt.TokenProvider;
import santaOps.santaLog.domain.User;
import santaOps.santaLog.dto.AddUserRequest;
import santaOps.santaLog.dto.UserCacheDto;
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

        CookieUtil.getCookie(request, "ACCESS_TOKEN")
                .map(Cookie::getValue)
                .ifPresent(token -> {
                    try {
                        Long userId = tokenProvider.getUserId(token);
                        refreshTokenService.deleteByUserId(userId);
                    } catch (Exception e) {
                        System.err.println(">>> 로그아웃 중 Redis 삭제 실패: " + e.getMessage());
                    }
                });

        // 2. CookieUtil을 사용하여 브라우저 쿠키 확실히 삭제
        // (내부적으로 ResponseCookie + Domain + SameSite=None 처리됨)
        CookieUtil.deleteCookie(request, response, "ACCESS_TOKEN");
        CookieUtil.deleteCookie(request, response, "refresh_token");
        CookieUtil.deleteCookie(request, response, "JSESSIONID"); // 세션 쿠키도 함께 제거

        return "redirect:https://santalog.cloud:31443/login";


    }

    @GetMapping("/users/count")
    public ResponseEntity<Long> getUserCount() {
        return ResponseEntity.ok(userService.countUsers());
    }

    @GetMapping("/user/{id}")
    @ResponseBody
    public UserCacheDto getUserInfo(@PathVariable Long id) {
        User user = userService.findById(id);
        return UserCacheDto.from(user);
    }

}
