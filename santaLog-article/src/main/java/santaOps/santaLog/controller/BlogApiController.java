package santaOps.santaLog.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import santaOps.santaLog.domain.Article;
import santaOps.santaLog.dto.AddArticleRequest;
import santaOps.santaLog.dto.ArticleResponse;
import santaOps.santaLog.dto.UpdateArticleRequest;
import santaOps.santaLog.service.BlogService;
import santaOps.santaLog.service.S3Service;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/")
public class BlogApiController {

    private final BlogService blogService;
    private final S3Service s3Service;

    /**
     * 글 등록 (POST)
     */
    @PostMapping("articles")
    public ResponseEntity<Article> addArticle(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam(value = "isNotice", required = false) Boolean isNotice,
            @RequestPart(value = "image", required = false) MultipartFile image,
            Principal principal
    ) throws IOException {

        if (principal == null) {
            throw new RuntimeException("로그인 정보가 없습니다.");
        }
        if (title == null || title.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        // 로컬 저장 대신 S3 업로드 후 URL 반환받음
        String imageUrl = null;
        if (image != null && !image.isEmpty()) {
            imageUrl = s3Service.upload(image);
        }

        boolean isNoticeValue = (isNotice != null) && isNotice;

        AddArticleRequest request = new AddArticleRequest(title, content, imageUrl, isNoticeValue, false);

        String email = principal.getName();
        Article savedArticle = blogService.save(request, email);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedArticle);
    }

    /**
     * 글 목록 조회 (GET)
     */
    @GetMapping("articles")
    public ResponseEntity<List<ArticleResponse>> findAllArticles(){
        List<ArticleResponse> articles = blogService.findAll()
                .stream()
                .map(ArticleResponse::new)
                .toList();
        return ResponseEntity.ok().body(articles);
    }

    /**
     * 글 단건 조회 (GET)
     */
    @GetMapping("articles/{id}")
    public ResponseEntity<ArticleResponse> findArticle(@PathVariable long id){
        Article article = blogService.findById(id);
        return ResponseEntity.ok().body(new ArticleResponse(article));
    }

    /**
     * 글 삭제 (DELETE)
     */
    @DeleteMapping("articles/{id}")
    public ResponseEntity<Void> deleteArticle (@PathVariable long id){
        blogService.delete(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 글 수정 (PUT)
     */
    @PutMapping("articles/{id}")
    public ResponseEntity<Article> updateArticle(
            @PathVariable long id,
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam(value = "isNotice", required = false) Boolean isNotice,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) throws IOException {

        Article currentArticle = blogService.findById(id);

        // 새로운 이미지가 들어오면 S3 업로드, 아니면 기존 URL 유지
        String imageUrl = currentArticle.getThumbnailUrl();
        if (image != null && !image.isEmpty()) {
            imageUrl = s3Service.upload(image);
        }

        boolean isNoticeValue = (isNotice != null) && isNotice;
        boolean isWarnedValue = currentArticle.getIsWarned();

        UpdateArticleRequest request = new UpdateArticleRequest(title, content, imageUrl, isNoticeValue, isWarnedValue);
        Article updatedArticle = blogService.update(id, request);

        return ResponseEntity.ok().body(updatedArticle);
    }

    /**
     * 게시글 경고 주기 (PUT)
     */
    @PutMapping("articles/{id}/warn")
    public ResponseEntity<Void> warnArticle(@PathVariable Long id) {
        blogService.warnArticle(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 경고 취소 (DELETE)
     */
    @DeleteMapping("articles/{id}/warn")
    public ResponseEntity<Void> unWarnArticle(@PathVariable Long id) {
        blogService.unWarnArticle(id);
        return ResponseEntity.ok().build();
    }
}