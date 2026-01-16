package santaOps.santaLog.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import santaOps.santaLog.service.BlogService;

@RequiredArgsConstructor
@Controller
@RequestMapping("/admin")
public class AdminController {

//    private final UserService userService;
    private final BlogService blogService;

    @GetMapping("/login")
    public String loginPage(HttpServletResponse response) {
        Cookie cookie = new Cookie("ACCESS_TOKEN", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
        return "admin/login";
    }


    @GetMapping("/stats")
    public String showStatistics(Model model) {
        model.addAttribute("totalUsers", null);
        model.addAttribute("totalArticles", blogService.countArticles());

        return "admin/stats";
    }

}
