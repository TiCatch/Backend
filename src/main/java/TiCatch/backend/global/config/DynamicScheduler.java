package TiCatch.backend.global.config;

import TiCatch.backend.domain.ticketing.entity.TicketingLevel;
import TiCatch.backend.domain.ticketing.service.TicketingBatchProcessService;
import TiCatch.backend.domain.ticketing.service.TicketingSeatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.*;
import java.util.concurrent.*;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import static TiCatch.backend.global.constant.SchedulerConstants.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class DynamicScheduler {

    private final TicketingBatchProcessService ticketingBatchProcessService;
    private final TicketingSeatService ticketingSeatService;

    private final ConcurrentHashMap<Long, ScheduledExecutorService> schedulerMap = new ConcurrentHashMap<>();
    private final Map<Long, ScheduledFuture<?>> inProgressSchedulerMap = new ConcurrentHashMap<>();

    private static final Map<String, Integer> SEAT_WEIGHTS;

    //서버 시작할때 파일을 읽고 메모리에 저장
    static {
        SEAT_WEIGHTS = loadSeatWeights();
    }

    public void startScheduler(Long ticketingId) {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            try {
                Long targetCount = ticketingBatchProcessService.processBatchInWaitingQueue(ticketingId, BATCH_SIZE);
                if (targetCount == 0L) {
                    stopScheduler(ticketingId);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, DYNAMIC_SCHEDULER_INITIAL_DELAY, DYNAMIC_SCHEDULER_PERIOD, TimeUnit.SECONDS);

        schedulerMap.put(ticketingId, scheduler);
    }

    public void stopScheduler(Long ticketingId) {
        ScheduledExecutorService scheduler = schedulerMap.get(ticketingId);
        if (scheduler != null) {
            scheduler.shutdown();
            schedulerMap.remove(ticketingId);
        }
    }

    public void stopNowScheduler(Long ticketingId) {
        ScheduledFuture<?> scheduledFuture = inProgressSchedulerMap.get(ticketingId);
        if(scheduledFuture != null) {
            scheduledFuture.cancel(true);
            inProgressSchedulerMap.remove(ticketingId);
        }
    }

    private static Map<String, Integer> loadSeatWeights() {
        try {
            InputStream inputStream = new ClassPathResource("seat_weights.json").getInputStream();
            Map<String, Integer> weights = new ObjectMapper().readValue(inputStream, new TypeReference<>() {});
            log.info("좌석별 가중치 데이터 파일 로드 성공 :  {} 개 좌석 ", weights.size());
            return weights;
        } catch (Exception e) {
            log.error("좌석별 가중치 데이터 파일 로드 실패", e);
            return new HashMap<>();
        }
    }

    public void startTicketingScheduler(Long ticketingId, TicketingLevel level) {
        if (schedulerMap.containsKey(ticketingId)) {
            log.warn("이미 실행 중인 티켓팅 스케줄러: ID={}", ticketingId);
            return;
        }

        int ticketCount = getTicketCountByLevel(level);
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        ScheduledFuture<?> scheduledFuture = scheduler.scheduleAtFixedRate(() -> {
            ticketingSeatService.processSeatReservation(ticketingId, ticketCount, SEAT_WEIGHTS,
                    () -> stopNowScheduler(ticketingId));
        }, 0, 1, TimeUnit.SECONDS);

        schedulerMap.put(ticketingId, scheduler);
        inProgressSchedulerMap.put(ticketingId, scheduledFuture);
        log.info("티켓팅 스케줄러 시작: 티켓팅 ID={}, Level={}, 초당 {}개 예약", ticketingId, level, ticketCount);
    }

    // 게임 난이도별 초당 예약 개수
    private int getTicketCountByLevel(TicketingLevel level) {
        return switch (level) {
            case EASY -> 2;
            case NORMAL -> 5;
            case HARD -> 10;
        };
    }
}
