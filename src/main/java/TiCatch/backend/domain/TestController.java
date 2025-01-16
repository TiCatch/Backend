package TiCatch.backend.domain;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/")
    public String test() {
        return "Hello TiCatch";
    }


    private static final Queue<TicketRequest> queue = new ConcurrentLinkedQueue<>();
    private static final Object lock = new Object();

    // 티켓 요청을 대기열에 추가하고, 몇 번째로 대기 중인지 알려줌
    @GetMapping("/request")
    public Mono<String> requestTicket(@RequestParam String userId) {
        TicketRequest request = new TicketRequest(userId, System.currentTimeMillis());
        queue.add(request);

        // 대기열 순위 로깅
        int position = queue.size();  // 요청이 대기열에서 몇 번째인지를 확인
        log.info(userId + " added to queue. Current position: " + position);

        // 요청이 대기열에 몇 번째로 추가되었는지 반환
        return Mono.just("Request received. You are in position " + position);
    }

    // 대기열을 처리하는 메소드
    @Scheduled(fixedRate = 1000)  // 1초마다 대기열 처리
    public void processTickets() {
        synchronized (lock) {
            if (!queue.isEmpty()) {
                TicketRequest request = queue.poll();  // 대기열에서 첫 번째 요청 꺼내기
                if (request != null) {
                    long waitTime = System.currentTimeMillis() - request.getRequestTime();
                    log.info("Processing request for " + request.getUserId() + ". Waited " + waitTime + " ms.");
                }
            }
        }
    }

    // 티켓 요청 클래스
    public static class TicketRequest {
        private final String userId;
        private final long requestTime;

        public TicketRequest(String userId, long requestTime) {
            this.userId = userId;
            this.requestTime = requestTime;
        }

        public String getUserId() {
            return userId;
        }

        public long getRequestTime() {
            return requestTime;
        }
    }
}
