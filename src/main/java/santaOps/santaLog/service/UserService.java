package santaOps.santaLog.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import santaOps.santaLog.config.jwt.TokenProvider;
import santaOps.santaLog.domain.Role;
import santaOps.santaLog.domain.User;
import santaOps.santaLog.dto.AddUserRequest;
import santaOps.santaLog.dto.AdminLoginRequest;
import santaOps.santaLog.repository.UserRepository;

import java.time.Duration;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;

    public Long save(AddUserRequest dto) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        try {

            return userRepository.save(
                    User.builder()
                            .email(dto.getEmail())
                            .password(encoder.encode(dto.getPassword()))
                            .role(Role.USER)
                            .build()).getId();

        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }
    }

    public User findById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Unexpected user"));
    }

    public User findByEmail(String email){
        return userRepository.findByEmail(email)
                .orElseThrow(()->new IllegalArgumentException("Unexpected user"));
    }


    private final BCryptPasswordEncoder passwordEncoder;

    public String adminLogin(AdminLoginRequest request) {

        User admin = authenticate(
                request.getEmail(),
                request.getPassword()
        );

        if (admin.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("관리자 권한이 없습니다.");
        }

        return tokenProvider.generateToken(
                admin,
                Duration.ofHours(2)
        );
    }

    public User authenticate(String email, String password) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException("ID_NOT_FOUND"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("PW_NOT_MATCH");
        }

        return user;
    }

}
