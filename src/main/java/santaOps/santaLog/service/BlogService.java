package santaOps.santaLog.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import santaOps.santaLog.domain.Article;
import santaOps.santaLog.dto.AddArticleRequest;
import santaOps.santaLog.dto.UpdateArticleRequest;
import santaOps.santaLog.repository.BlogRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class BlogService {

    private final BlogRepository blogRepository;


    public Article save(AddArticleRequest request, String userName) {
        return blogRepository.save(request.toEntity(userName));
    }

    public List<Article> findAll() {
        return blogRepository.findAll();
    }

    public Article findById(long id){
        return blogRepository.findById(id).orElseThrow(()-> new IllegalArgumentException("not found: " + id));
    }

    public void delete(long id){
        Article article = blogRepository.findById(id)
                .orElseThrow(()->new IllegalArgumentException("not found: " + id));

        authorizeArticleAuthor(article);
        blogRepository.delete(article);
    }

    @Transactional
    public Article update(long id, UpdateArticleRequest request) {
        Article article = blogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found : " + id));

        authorizeArticleAuthor(article);
        article.update(request.getTitle(), request.getContent(), request.getThumbnailUrl(),request.getIsNotice());

        return article;
    }

    private static void authorizeArticleAuthor(Article article){
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!article.getAuthor().equals(userName)){
            throw new IllegalArgumentException("not authorized");
        }
    }


}
