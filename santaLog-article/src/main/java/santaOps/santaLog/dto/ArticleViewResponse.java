package santaOps.santaLog.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import santaOps.santaLog.domain.Article;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
public class ArticleViewResponse {

    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private String thumbnailUrl;
    private boolean isNotice;
    private boolean isWarned;
    private String author;

    public ArticleViewResponse(Article article){
        this.id = article.getId();
        this.title = article.getTitle();
        this.content = article.getContent();
        this.createdAt = article.getCreatedAt();
        this.author = article.getAuthor();
        this.thumbnailUrl = article.getThumbnailUrl();
        this.isNotice = article.getIsNotice();
        this.isWarned = article.getIsWarned();
    }

}
