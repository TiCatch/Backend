package TiCatch.backend.global.config;

import TiCatch.backend.domain.ticketing.service.TicketingBatchProcessService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static TiCatch.backend.global.constant.SchedulerConstants.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class DynamicScheduler {

    private final TicketingBatchProcessService ticketingBatchProcessService;
    private final ConcurrentHashMap<Long, ScheduledExecutorService> schedulerMap = new ConcurrentHashMap<>();

    public void startScheduler(Long ticketingId) {
        if (schedulerMap.containsKey(ticketingId)) {
            return;
        }

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            try {
                Long targetCount = ticketingBatchProcessService.processBatchInWaitingQueue(ticketingId, BATCH_SIZE);
                if(targetCount == 0L) {
                    stopScheduler(ticketingId);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, INITIAL_DELAY, PERIOD, TimeUnit.SECONDS);

        schedulerMap.put(ticketingId, scheduler);
    }

    public void stopScheduler(Long ticketingId) {
        ScheduledExecutorService scheduler = schedulerMap.get(ticketingId);
        if (scheduler != null) {
            scheduler.shutdown();
            schedulerMap.remove(ticketingId);
        }
    }
}