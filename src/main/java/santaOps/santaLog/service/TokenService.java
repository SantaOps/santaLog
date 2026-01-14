package santaOps.santaLog.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import santaOps.santaLog.config.jwt.TokenProvider;
import santaOps.santaLog.config.oauth.OAuth2SuccessHandler;
import santaOps.santaLog.domain.User;

@RequiredArgsConstructor
@Service
public class TokenService {

    private final TokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;

    public String createNewAccessToken(String refreshToken) {
        // 토큰 유효성 검사에 실패하면 예외 발생
        if(!tokenProvider.validToken(refreshToken)) {
            throw new IllegalArgumentException("Unexpected token");
        }

        Long userId = refreshTokenService.findByRefreshToken(refreshToken).getUserId();
        User user = userService.findById(userId);
        return tokenProvider.generateToken(user, OAuth2SuccessHandler.ACCESS_TOKEN_DURATION);
    }

}
