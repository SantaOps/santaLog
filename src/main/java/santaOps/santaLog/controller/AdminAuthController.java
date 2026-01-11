package santaOps.santaLog.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import santaOps.santaLog.domain.Role;
import santaOps.santaLog.domain.User;
import santaOps.santaLog.service.UserService;
@Controller
@RequiredArgsConstructor
@RequestMapping("/auth/admin")
public class AdminAuthController {

    private final UserService userService;

    @PostMapping("/login")
    public String adminLogin(
            @RequestParam String email,
            @RequestParam String password
    ) {
        try {
            User admin = userService.authenticate(email, password);

            if (admin.getRole() != Role.ADMIN) {
                return "redirect:/admin/login?error=not_admin";
            }

            return "redirect:/articles";

        } catch (IllegalArgumentException e) {

            if ("ID_NOT_FOUND".equals(e.getMessage())) {
                return "redirect:/admin/login?error=id";
            }

            if ("PW_NOT_MATCH".equals(e.getMessage())) {
                return "redirect:/admin/login?error=pw";
            }

            return "redirect:/admin/login?error=unknown";
        }
    }

}
