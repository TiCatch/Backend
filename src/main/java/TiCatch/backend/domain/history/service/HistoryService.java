package TiCatch.backend.domain.history.service;

import TiCatch.backend.domain.history.dto.response.HistoryPagingResponse;
import TiCatch.backend.domain.history.repository.HistoryRepository;
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

    public Page<HistoryPagingResponse> getHistoryListWithPaged(Long userId, Pageable pageable) {
         return historyRepository.findHistoryByUserIdWithPaged(userId, pageable);
    }
}
