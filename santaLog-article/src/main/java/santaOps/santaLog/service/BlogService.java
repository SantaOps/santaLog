package santaOps.santaLog.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import santaOps.santaLog.domain.Article;
import santaOps.santaLog.dto.AddArticleRequest;
import santaOps.santaLog.dto.UpdateArticleRequest;
import santaOps.santaLog.dto.UserCacheDto;
import santaOps.santaLog.repository.BlogRepository;

import java.time.Duration;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class BlogService {

    private final BlogRepository blogRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RestTemplate restTemplate = new RestTemplate();

    private static final String AUTH_SERVER_URL = "https://santalog.cloud:31443/user/";

    public Article save(AddArticleRequest request, String userName) {
        if (Boolean.TRUE.equals(request.getIsNotice())) {
            authorizeAdmin();
        }

        Long userId = Long.parseLong(userName);
        UserCacheDto userCache = getUserCacheWithLookAside(userId);

        String email = (userCache != null) ? userCache.getEmail() : userName;

        return blogRepository.save(request.toEntity(email));
    }

    public List<Article> findAll() {
        return blogRepository.findAll();
    }

    public Article findById(long id) {
        return blogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found: " + id));
    }

    public void delete(long id) {
        Article article = blogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found: " + id));

        authorizeArticleAuthor(article);
        blogRepository.delete(article);
    }

    @Transactional
    public Article update(long id, UpdateArticleRequest request) {
        Article article = blogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found: " + id));

        authorizeArticleAuthor(article);
        article.update(
                request.getTitle(),
                request.getContent(),
                request.getThumbnailUrl(),
                request.getIsNotice(),
                request.getIsWarned()
        );
        return article;
    }

    @Transactional
    public void warnArticle(Long id) {
        authorizeAdmin();
        Article article = blogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("글이 없습니다: " + id));
        article.warn();
    }

    @Transactional
    public void unWarnArticle(Long id) {
        authorizeAdmin();
        Article article = blogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found: " + id));
        article.unWarn();
    }

    // ================== 권한 체크 ==================

    private void authorizeArticleAuthor(Article article) {
        Long userId = getCurrentUserId();
        UserCacheDto userCache = getUserCacheWithLookAside(userId);

        // ADMIN은 통과, 일반 유저는 작성자 email 비교
        if (!"ADMIN".equals(userCache.getRole()) &&
                !article.getAuthor().equals(userCache.getEmail())) {
            throw new IllegalArgumentException("권한이 없습니다.");
        }
    }

    private void authorizeAdmin() {
        Long userId = getCurrentUserId();
        UserCacheDto userCache = getUserCacheWithLookAside(userId);

        if (!"ADMIN".equals(userCache.getRole())) {
            throw new IllegalArgumentException("관리자 권한이 필요합니다.");
        }
    }

    /**
     * Look-aside 캐싱 (ID 기반)
     */
    public UserCacheDto getUserCacheWithLookAside(Long userId) {
        if (userId == null) {
            log.debug("userId가 null이므로 캐시 조회를 건너뜁니다.");
            return null;
        }

        String cacheKey = "USER:" + userId;

        // 1. Redis 캐시 확인
        UserCacheDto userCache = (UserCacheDto) redisTemplate.opsForValue().get(cacheKey);

        if (userCache == null) {
            log.info("Cache Miss! Auth 서버에서 사용자 조회. userId={}", userId);

            try {
                userCache = restTemplate.getForObject(
                        AUTH_SERVER_URL + userId,
                        UserCacheDto.class
                );

                if (userCache != null) {
                    redisTemplate.opsForValue()
                            .set(cacheKey, userCache, Duration.ofHours(1));
                } else {
                    log.warn("Auth 서버에 해당 유저가 존재하지 않습니다. userId={}", userId);
                }
            } catch (Exception e) {
                log.error("Auth 서버 호출 실패 - userId={}", userId, e);
                return null;
            }
        } else {
            log.info("Cache Hit!!! Redis에서 유저 정보를 가져왔습니다. userId={}, email={}", userId, userCache.getEmail());
        }

        return userCache;
    }

    public Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            return null; // 로그인하지 않은 경우
        }
        return Long.parseLong(auth.getName());
    }

    public long countArticles() {
        return blogRepository.count();
    }
}
