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

import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/")
public class BlogApiController {

    private final BlogService blogService;
    private static final String UPLOAD_DIR = "C:/Users/dnjft/SpringProject/santaLog-dev/src/main/resources/static/img/";

    /**
     * 글 등록 (POST)
     */
    @PostMapping("articles")
    public ResponseEntity<Article> addArticle(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam(value = "isNotice", required = false) Boolean isNotice, // 파라미터 받기
            @RequestPart(value = "image", required = false) MultipartFile image,
            Principal principal
    ) throws IOException {

        if (principal == null) {
            throw new RuntimeException("로그인 정보가 없습니다.");
        }

        String fileName = null;
        if (image != null && !image.isEmpty()) {
            fileName = saveImage(image);
        }

        // [수정] null 체크 후 boolean 변환 (체크 안하면 null로 올 수 있음 -> false로 처리)
        boolean isNoticeValue = (isNotice != null) && isNotice;

        AddArticleRequest request = new AddArticleRequest(title, content, fileName, isNoticeValue);

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
            @RequestParam(value = "isNotice", required = false) Boolean isNotice, // [추가] 수정 시에도 공지 여부 받기
            @RequestPart(value = "image", required = false) MultipartFile image
    ) throws IOException {

        String fileName = null;
        if (image != null && !image.isEmpty()) {
            fileName = saveImage(image);
        }

        // [수정] null 체크
        boolean isNoticeValue = (isNotice != null) && isNotice;

        UpdateArticleRequest request = new UpdateArticleRequest(title, content, fileName, isNoticeValue);

        Article updatedArticle = blogService.update(id, request);

        return ResponseEntity.ok().body(updatedArticle);
    }

    /**
     * [내부 메서드] 실제 파일을 디스크에 저장하고 저장된 파일명을 반환
     */
    private String saveImage(MultipartFile image) throws IOException {
        if (image.isEmpty()) return null;

        File dir = new File(UPLOAD_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 파일명 중복 방지를 위한 UUID
        String originalFilename = image.getOriginalFilename();
        String storeFileName = UUID.randomUUID() + "_" + originalFilename;

        // 파일 저장
        File dest = new File(UPLOAD_DIR + storeFileName);
        image.transferTo(dest);

        return storeFileName;
    }
}