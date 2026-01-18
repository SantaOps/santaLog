package santaOps.santaLog.controller;

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

    private final BlogService blogService;

    @GetMapping("/stats")
    public String showStatistics(Model model) {
        model.addAttribute("totalArticles", blogService.countArticles());
        // TODO 유저 수는 나중에 Auth 서버와 통신(API)해서 가져와야 하므로 일단 null 유지
        model.addAttribute("totalUsers", 0);

        return "admin/stats"; // templates/admin/stats.html 호출
    }
}