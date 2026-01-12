package santaOps.santaLog.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import santaOps.santaLog.domain.Article;

@Getter
@RequiredArgsConstructor
public class ArticleListViewResponse {

    private final Long id;
    private final String title;
    private final String content;
    private final String thumbnailUrl;

    public ArticleListViewResponse(Article article){
        this.id = article.getId();
        this.title = article.getTitle();
        this.content = article.getContent();
        this.thumbnailUrl = article.getThumbnailUrl();
    }

}
