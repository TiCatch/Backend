package TiCatch.backend.domain.history.repository;

import TiCatch.backend.domain.history.dto.response.HistoryPagingResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface HistoryCustomRepository {
    Page<HistoryPagingResponse> findHistoryByUserIdWithPaged(Long userId, Pageable pageable);
}
