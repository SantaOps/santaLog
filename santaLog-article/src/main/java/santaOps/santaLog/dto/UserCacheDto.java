package santaOps.santaLog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserCacheDto implements Serializable {

    private Long id;
    private String email;
    private String role; // "ADMIN" 또는 "USER"

}