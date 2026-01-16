package santaOps.santaLog.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import santaOps.santaLog.config.oauth.OAuth2SuccessHandler;
import santaOps.santaLog.dto.CreateAccessTokenRequest;
import santaOps.santaLog.dto.CreateAccessTokenResponse;
import santaOps.santaLog.service.TokenService;
import santaOps.santaLog.util.CookieUtil;

@RequiredArgsConstructor
@RestController
public class TokenApiController {
    private final TokenService tokenService;

    @PostMapping("/api/token")
    public ResponseEntity<CreateAccessTokenResponse> createNewAccessToken(
            @RequestBody CreateAccessTokenRequest request,
            HttpServletResponse response) {

        String newAccessToken = tokenService.createNewAccessToken(request.getRefreshToken());

        // SuccessHandler에 정의된 ACCESS_TOKEN_DURATION(1일)
        int cookieMaxAge = (int) OAuth2SuccessHandler.ACCESS_TOKEN_DURATION.toSeconds();

        // 쿠키 이름도 상수를 활용해서 "ACCESS_TOKEN"으로 일치시킴
        CookieUtil.addCookie(response, OAuth2SuccessHandler.ACCESS_TOKEN_COOKIE_NAME, newAccessToken, cookieMaxAge);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CreateAccessTokenResponse(newAccessToken));
    }
}