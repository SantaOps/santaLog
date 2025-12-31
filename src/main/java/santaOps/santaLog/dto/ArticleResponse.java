package santaOps.santaLog.dto;

import lombok.Getter;
import santaOps.santaLog.domain.Article;
import java.time.LocalDateTime;

@Getter
public class ArticleResponse {


    private final String title;
    private final String content;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public ArticleResponse(Article article){
        this.title = article.getTitle();
        this.content = article.getContent();
        this.createdAt = article.getCreatedAt();
        this.updatedAt = article.getUpdateAt();

    }


}
