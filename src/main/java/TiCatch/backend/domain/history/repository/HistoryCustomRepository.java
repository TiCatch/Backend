package TiCatch.backend.domain.history.repository;

import TiCatch.backend.domain.history.dto.response.LevelHistoryResponse;
import TiCatch.backend.domain.history.dto.response.TicketingHistoryPagingResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface HistoryCustomRepository {
    Page<TicketingHistoryPagingResponse> findHistoryByUserIdWithPaged(Long userId, Pageable pageable);
    LevelHistoryResponse findHistoryByUserIdWithLevelCounts(Long userId);
}
