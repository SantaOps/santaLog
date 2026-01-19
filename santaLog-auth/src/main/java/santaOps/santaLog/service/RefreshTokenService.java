package santaOps.santaLog.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import santaOps.santaLog.domain.RefreshToken;
import santaOps.santaLog.repository.RefreshTokenRepository;

@RequiredArgsConstructor
@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    // 토큰으로 조회 (토큰 재발급 시 사용)
    public RefreshToken findByRefreshToken(String refreshToken) {
        return refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected token"));
    }

    // 로그인 시 토큰 저장 또는 업데이트
    @Transactional
    public void saveOrUpdate(Long userId, String newRefreshToken) {
        RefreshToken token = refreshTokenRepository.findById(userId)
                .map(t -> {
                    t.update(newRefreshToken);
                    return t;
                })
                .orElse(new RefreshToken(userId, newRefreshToken));

        refreshTokenRepository.save(token);
    }
}