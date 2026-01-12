package santaOps.santaLog.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import santaOps.santaLog.domain.Article;
import santaOps.santaLog.dto.ArticleResponse;
import santaOps.santaLog.dto.ArticleViewResponse;
import santaOps.santaLog.service.BlogService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Controller
public class BlogViewController {

    private final BlogService blogService;

    @GetMapping("/articles")
    public String getArticles(Model model) {
        List<ArticleResponse> allArticles = blogService.findAll()
                .stream()
                .map(ArticleResponse::new)
                .toList();

        // 1. 공지사항과 일반글 분리
        List<ArticleResponse> notices = allArticles.stream()
                .filter(ArticleResponse::isNotice) // 공지사항만 필터링
                .collect(Collectors.toList());

        List<ArticleResponse> regularArticles = allArticles.stream()
                .filter(a -> !a.isNotice()) // 일반글만 필터링
                .collect(Collectors.toList());

        // 2. 공지사항을 3개씩 묶기 (Chunking)
        List<List<ArticleResponse>> noticeChunks = new ArrayList<>();
        int chunkSize = 3;
        for (int i = 0; i < notices.size(); i += chunkSize) {
            noticeChunks.add(notices.subList(i, Math.min(i + chunkSize, notices.size())));
        }

        // 3. 모델에 담기
        model.addAttribute("noticeChunks", noticeChunks); // 3개씩 묶인 공지
        model.addAttribute("articles", regularArticles);  // 나머지 일반 글

        return "articleList";
    }

    @GetMapping("/articles/{id}")
    public String getArticle(@PathVariable Long id, Model model){
        Article article = blogService.findById(id);
        model.addAttribute("article", new ArticleViewResponse(article));

        return "article";
    }

    @GetMapping("/new-article")
    public String newArticle(@RequestParam(required = false) Long id, Model model){
        if (id == null){
            // 게시글의 id가 있으면 수정으로
            model.addAttribute("article", new ArticleViewResponse());
        }else{
            Article article = blogService.findById(id);
            model.addAttribute("article", new ArticleViewResponse(article));
        }

        return "newArticle.html";

    }

}
