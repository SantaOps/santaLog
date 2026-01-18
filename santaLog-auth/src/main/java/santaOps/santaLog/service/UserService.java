package santaOps.santaLog.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import santaOps.santaLog.config.jwt.TokenProvider;
import santaOps.santaLog.domain.Role;
import santaOps.santaLog.domain.User;
import santaOps.santaLog.dto.AddUserRequest;
import santaOps.santaLog.dto.AdminLoginRequest;
import santaOps.santaLog.repository.UserRepository;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private TokenProvider tokenProvider;

    public void setTokenProvider(@Lazy TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    /**
     * 회원 가입 (기본 USER 권한)
     */
    public Long save(AddUserRequest dto) {
        try {
            return userRepository.save(
                    User.builder()
                            .email(dto.getEmail())
                            .password(passwordEncoder.encode(dto.getPassword()))
                            .role(Role.USER)
                            .build()).getId();
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }
    }

    /**
     * 관리자 로그인 및 토큰 발급
     */
    public String adminLogin(AdminLoginRequest request) {
        User admin = authenticate(request.getEmail(), request.getPassword());

        if (admin.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("관리자 권한이 없습니다.");
        }

        return tokenProvider.generateToken(admin, Duration.ofHours(2));
    }

    /**
     * 이메일/비밀번호 검증 로직
     */
    public User authenticate(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("ID_NOT_FOUND"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("PW_NOT_MATCH");
        }

        return user;
    }

    /**
     * [관리자용] 비밀번호 강제 업데이트 (DB 동기화용)
     */
    @Transactional
    public void updateAdminPassword(String email, String rawPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("USER_NOT_FOUND"));
        user.setPassword(passwordEncoder.encode(rawPassword));
    }

    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected user"));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected user"));
    }

    public long countUsers() {
        return userRepository.count();
    }
}