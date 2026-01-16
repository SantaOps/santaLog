package santaOps.santaLog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import santaOps.santaLog.domain.Article;

public interface BlogRepository extends JpaRepository<Article, Long> {
}
