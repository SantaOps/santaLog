package santaOps.santaLog.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import santaOps.santaLog.domain.RefreshToken;
import santaOps.santaLog.repository.RefreshTokenRepository;

@RequiredArgsConstructor
@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshToken findByRefreshToken(String refreshToken){
        return refreshTokenRepository.findByRefreshToken(refreshToken).orElseThrow(()->new IllegalArgumentException("Unexpected token"));
    }

}
