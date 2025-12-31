package santaOps.santaLog.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import santaOps.santaLog.domain.Article;
import santaOps.santaLog.dto.AddArticleRequest;
import santaOps.santaLog.dto.ArticleResponse;
import santaOps.santaLog.service.BlogService;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class BlogApiController {
//TODO: 응답 형태 공통으로 묶기
//TODO: base 주소 선언하기

    private final BlogService blogService;

    @PostMapping("/api/articles")
    public ResponseEntity<Article> addArticle(@RequestBody AddArticleRequest request){
        Article savedArticle = blogService.save(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedArticle);
    }

    @GetMapping("/api/articles")
    public ResponseEntity<List<ArticleResponse>> findAllArticles(){
        List<ArticleResponse> articles = blogService.findAll()
                .stream()
                .map(ArticleResponse::new)
                .toList();

        return ResponseEntity.ok().body(articles);
    }



}
