package santaOps.santaLog.dto;

import lombok.Getter;
import santaOps.santaLog.domain.Article;
import java.time.LocalDateTime;

@Getter
public class ArticleResponse {
    private final Long id;
    private final String title;
    private final String content;
    private final String thumbnailUrl;
    private final boolean isNotice;

    public ArticleResponse(Article article) {
        this.id = article.getId();
        this.title = article.getTitle();
        this.content = article.getContent();
        this.thumbnailUrl = article.getThumbnailUrl();
        this.isNotice = article.getIsNotice();
    }
}