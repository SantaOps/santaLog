package santaOps.santaLog.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@AllArgsConstructor
@RedisHash(value = "refreshToken", timeToLive = 1209600) // 14일 (초 단위: 14 * 24 * 60 * 60)
public class RefreshToken {

    @Id // Redis의 Key (refreshToken:1)
    private Long userId;

    @Indexed // 토큰 값으로 조회가 가능하도록 인덱스 설정
    private String refreshToken;

    public RefreshToken update(String newRefreshToken) {
        this.refreshToken = newRefreshToken;
        return this;
    }
}