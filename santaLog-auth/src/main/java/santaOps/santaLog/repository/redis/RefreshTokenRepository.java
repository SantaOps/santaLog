package santaOps.santaLog.repository.redis;

import org.springframework.data.repository.CrudRepository;
import santaOps.santaLog.domain.RefreshToken;
import java.util.Optional;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByRefreshToken(String refreshToken);

}