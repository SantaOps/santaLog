package santaOps.santaLog.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import santaOps.santaLog.dto.AddUserRequest;
import santaOps.santaLog.service.UserService;
import jakarta.servlet.http.Cookie;

@RequiredArgsConstructor
@Controller
public class UserApiController {

    private final UserService userService;

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

        // 서버 메모리의 인증정보 제거
        new SecurityContextLogoutHandler().logout(
                request, response,
                SecurityContextHolder.getContext().getAuthentication()
        );

        // 2. 브라우저 ACCESS_TOKEN 삭제
        Cookie accessTokenCookie = new Cookie("ACCESS_TOKEN", null);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(0); // 이 설정이 핵심!
        response.addCookie(accessTokenCookie);

        // 브라우저의 refresh_token 삭제
        Cookie refreshCookie = new Cookie("refresh_token", null);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(0);
        response.addCookie(refreshCookie);

        return "redirect:http://localhost:8080/login";
    }

}
