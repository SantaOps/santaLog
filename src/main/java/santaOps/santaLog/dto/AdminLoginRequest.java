package santaOps.santaLog.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AdminLoginRequest {

    private String email;     // 관리자 ID (email 기반)
    private String password;  // 관리자 비밀번호
}
