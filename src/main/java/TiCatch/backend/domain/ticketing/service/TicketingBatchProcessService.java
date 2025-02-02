package TiCatch.backend.domain.ticketing.service;

import TiCatch.backend.domain.auth.service.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketingBatchProcessService {

    private final RedisService redisService;

    @Transactional
    public Long processBatchInWaitingQueue(Long ticketingId, int batchSize) {
        Set<ZSetOperations.TypedTuple<String>> batch = redisService.getBatchFromQueue(ticketingId, batchSize);
        if (batch == null || batch.isEmpty()) {
            return 0L;
        }
        redisService.removeBatchFromQueue(ticketingId, batchSize);
        return 1L;
    }
}
