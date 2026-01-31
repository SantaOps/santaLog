package santaOps.santaLog.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.util.SerializationUtils;

import java.util.Base64;
import java.util.Optional;

public class CookieUtil {

    public static Optional<Cookie> getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    return Optional.of(cookie);
                }
            }
        }
        return Optional.empty();
    }

    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        // new Cookie() 대신 ResponseCookie 사용 (SameSite 설정을 위함)
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .path("/")
                .httpOnly(true)
                .secure(true)             // SameSite=None 인 경우 필수
                .sameSite("None")         // OAuth2 리다이렉트 시 쿠키 유실 방지 필수 설정
                .domain("santalog.cloud") // www 제거하여 도메인 일치시킴
                .maxAge(maxAge)
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }

    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return;

        for (Cookie cookie : cookies) {
            if (name.equals(cookie.getName())) {
                ResponseCookie deleteCookie = ResponseCookie.from(name, "")
                        .path("/")
                        .httpOnly(true)
                        .secure(true)
                        .sameSite("None")
                        .domain("santalog.cloud") // 생성할 때 넣었던 도메인과 반드시 일치해야 함
                        .maxAge(0) // 즉시 만료
                        .build();
                response.addHeader("Set-Cookie", deleteCookie.toString());
            }
        }
    }

    public static String serialize(Object obj) {
        return Base64.getUrlEncoder()
                .encodeToString(SerializationUtils.serialize(obj));
    }

    public static <T> T deserialize(Cookie cookie, Class<T> cls) {
        if (cookie == null) {
            System.out.println("Cookie is null during deserialization");
            return null;
        }
        return cls.cast(SerializationUtils.deserialize(
                Base64.getUrlDecoder().decode(cookie.getValue())));
    }
}