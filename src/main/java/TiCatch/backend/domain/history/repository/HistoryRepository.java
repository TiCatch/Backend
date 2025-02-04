package TiCatch.backend.domain.history.repository;

import TiCatch.backend.domain.history.entity.History;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoryRepository extends JpaRepository<History, Long> {
}