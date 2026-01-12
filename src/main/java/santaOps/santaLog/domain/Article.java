package santaOps.santaLog.domain;


import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id",updatable = false)
    private Long id;

    @Column(name="title", nullable = false)
    private String title;

    @Column(name="content", nullable = false)
    private String content;

    @CreatedDate
    @Column(name="created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name="updated_at")
    private LocalDateTime updatedAt;

    @Column(name="author", nullable = false)
    private String author;

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @Builder
    public Article(String author, String title, String content, String thumbnailUrl){
        this.author = author;
        this.title = title;
        this.content = content;
        this.thumbnailUrl = thumbnailUrl;
    }

    public void update(String title, String content, String thumbnailUrl) {
        this.title = title;
        this.content = content;

        // 새로운 썸네일 경로가 들어왔을 때만 교체 (null이면 기존 이미지 유지)
        if (thumbnailUrl != null) {
            this.thumbnailUrl = thumbnailUrl;
        }
    }
}
