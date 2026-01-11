package santaOps.santaLog.service;

import com.nimbusds.oauth2.sdk.TokenResponse;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import santaOps.santaLog.domain.Role;
import santaOps.santaLog.domain.User;
import santaOps.santaLog.dto.AdminLoginRequest;

public class AdminService {

    @PostMapping("/auth/admin/login")
    public TokenResponse adminLogin(@RequestBody AdminLoginRequest request) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        User.builder()
                .email("admin@santa.com")
                .password(encoder.encode("admin1234"))
                .role(Role.ADMIN)
                .build();
        return null;
    }
}
