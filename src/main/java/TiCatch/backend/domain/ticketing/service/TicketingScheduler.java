package TiCatch.backend.domain.ticketing.service;

import TiCatch.backend.domain.ticketing.entity.TicketingLevel;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Service
@Slf4j
public class TicketingScheduler {

    private final StringRedisTemplate redisTemplate;
    private final Map<Long, Timer> activeSchedulers = new HashMap<>();
    private static final Map<String, Integer> SEAT_WEIGHTS;

    static {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Integer> seatWeights;
        try {
            seatWeights = objectMapper.readValue(
                    new File("src/main/resources/seat_weights.json"),
                    new TypeReference<>() {}
            );
            log.info("좌석 데이터 로드 완료. 총 {}개 좌석", seatWeights.size());
        } catch (IOException e) {
            log.error("좌석 가중치 데이터 로드 실패....ㅜㅜ", e);
            seatWeights = new HashMap<>();
        }
        SEAT_WEIGHTS = seatWeights;
    }

    public TicketingScheduler(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void startTicketingScheduler(Long ticketingId, TicketingLevel ticketingLevel) {
        if (activeSchedulers.containsKey(ticketingId)) {
            log.info("이미 실행 중인 티켓팅 스케줄러입니다. Ticketing ID={}", ticketingId);
            return;
        }

        // 난이도별 초당 예약 개수 설정
        final int ticketcount;
        switch (ticketingLevel) {
            case EASY:
                ticketcount = 2;
                break;
            case NORMAL:
                ticketcount = 5;
                break;
            case HARD:
            default:
                ticketcount = 10;
                break;
        }

        log.info("티켓팅 스케줄러 예약 : Ticketing ID={}, 난이도={}, 초당 {}개 예약",
                ticketingId, ticketingLevel, ticketcount);

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                processTicketing(ticketingId, ticketcount);
            }
        }, 0, 1000); // 1초마다 실행

        activeSchedulers.put(ticketingId, timer);
    }

    private void processTicketing(Long ticketingId, int maxCount) {
        log.info("티켓팅 스케줄러 실행 - Ticketing ID={}", ticketingId);

        String redisKey = "ticketingId:" + ticketingId;

        Map<Object, Object> seatStatusMap = redisTemplate.opsForHash().entries(redisKey);
        List<String> availableSeats = new ArrayList<>();
        for (Map.Entry<Object, Object> entry : seatStatusMap.entrySet()) {
            if (entry.getValue().equals("0")) {
                availableSeats.add(entry.getKey().toString());
            }
        }
        // 예약 가능한 좌석이 없으면 스케줄러 종료
        if (availableSeats.isEmpty()) {
            log.info(" 매진됐습니다!! 스케줄러 종료.");
            stopTicketingScheduler(ticketingId);
            return;
        }

        Map<Integer, List<String>> groupedSeats = new TreeMap<>(Collections.reverseOrder());
        for (String seat : availableSeats) {
            int weight = SEAT_WEIGHTS.getOrDefault(seat, 0);
            groupedSeats.computeIfAbsent(weight, k -> new ArrayList<>()).add(seat);
        }

        // 가중치가 같은 좌석들끼리 랜덤섞기
        List<String> sortedSeatList = new ArrayList<>();
        Random random = new Random();
        for (List<String> seats : groupedSeats.values()) {
            Collections.shuffle(seats, random);
            sortedSeatList.addAll(seats);
        }

        int count = 0;
        for (String seat : sortedSeatList) {
            if (count >= maxCount) break;

            if (seatStatusMap.get(seat).equals("0")) {
                redisTemplate.opsForHash().put(redisKey, seat, "1");
                log.info("### 예약 완료: Ticketing ID={}, Seat={}", ticketingId, seat);
                count++;
            }
        }

        log.info("1초에 {}개의 좌석을 예약 완료.", count);

        // 매진일 경우
        if (count == 0) {
            log.info("매진됐습니다!! 스케줄러 종료");
            stopTicketingScheduler(ticketingId);
        }
    }

    public void stopTicketingScheduler(Long ticketingId) {
        Timer timer = activeSchedulers.remove(ticketingId);
        if (timer != null) {
            timer.cancel();
            log.info("티켓팅 스케줄러 종료하기 : Ticketing ID={}", ticketingId);
        }
    }
}
