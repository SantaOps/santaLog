package santaOps.santaLog.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import santaOps.santaLog.config.jwt.TokenProvider;
import santaOps.santaLog.domain.Role;
import santaOps.santaLog.domain.User;
import santaOps.santaLog.dto.AdminLoginRequest;
import santaOps.santaLog.dto.AdminTokenResponse;
import santaOps.santaLog.service.UserService;
import java.time.Duration;
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/admin")
public class AdminAuthController {

    private final UserService userService;
    private final TokenProvider tokenProvider;

    @PostMapping("/login")
    public ResponseEntity<?> adminLogin(@RequestBody AdminLoginRequest request) {

        User admin = userService.authenticate(
                request.getEmail(),
                request.getPassword()
        );

        if (admin.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("관리자 권한이 없습니다.");
        }

        String accessToken = tokenProvider.generateToken(admin, Duration.ofHours(2));

        return ResponseEntity.ok(
                new AdminTokenResponse(accessToken)
        );
    }
}
