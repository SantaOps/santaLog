package santaOps.santaLog.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;
import santaOps.santaLog.service.BlogService;

@RequiredArgsConstructor
@Controller
@RequestMapping("/admin")
public class AdminController {

    private final BlogService blogService;
    private final RestTemplate restTemplate; // 주입받기

    @GetMapping("/stats")
    public String showStatistics(Model model) {
        model.addAttribute("totalArticles", blogService.countArticles());
        try {
            String authUrl = "http://localhost:8080/users/count";
            Long userCount = restTemplate.getForObject(authUrl, Long.class);
            model.addAttribute("totalUsers", userCount != null ? userCount : 0);
        } catch (Exception e) {
            model.addAttribute("totalUsers", 0);
        }
        return "admin/stats"; // templates/admin/stats.html 호출
    }
}