package TiCatch.backend.domain.history.service;

import TiCatch.backend.domain.history.dto.response.LevelHistoryResponse;
import TiCatch.backend.domain.history.dto.response.TicketingHistoryPagingResponse;
import TiCatch.backend.domain.history.repository.HistoryRepository;
import TiCatch.backend.domain.ticketing.entity.TicketingLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HistoryService {

    private final HistoryRepository historyRepository;

    public Page<TicketingHistoryPagingResponse> getTicketingHistoryWithPaged(Long userId, Pageable pageable) {
         return historyRepository.findHistoryByUserIdWithPaged(userId, pageable);
    }

    public LevelHistoryResponse getLevelHistory(Long userId) {
        return historyRepository.findHistoryByUserIdWithLevelCounts(userId);
    }

    public Page<TicketingHistoryPagingResponse> getTicketingHistoryByLevelWithPaged(Long userId, Pageable pageable, TicketingLevel ticketingLevel) {
        return historyRepository.findHistoryByUserIdAndLevelWithPaged(userId, pageable, ticketingLevel);
    }
}