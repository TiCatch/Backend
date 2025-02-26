package TiCatch.backend.domain.ticketing.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import static TiCatch.backend.global.constant.RedisConstants.TICKETING_SEAT_PREFIX;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketingSeatService {

    private final StringRedisTemplate redisTemplate;

    public void processSeatReservation(Long ticketingId, int maxCount, Map<String, Integer> seatWeights, Runnable stopSchedulerCallback) {
        String redisKey = TICKETING_SEAT_PREFIX + ticketingId;

        Map<Object, Object> seatStatusMap = redisTemplate.opsForHash().entries(redisKey);
        List<String> availableSeats = new ArrayList<>();
        for (var entry : seatStatusMap.entrySet()) {
            if (entry.getValue().equals("0")) {
                availableSeats.add(entry.getKey().toString());
            }
        }

        if (availableSeats.isEmpty()) {
            log.info("매진입니다! 스케줄러 중지");
            stopSchedulerCallback.run();
            return;
        }

        // 좌석을 가중치 기반으로 정렬하고 랜덤으로 예약
        Map<Integer, List<String>> groupedSeats = new TreeMap<>(Collections.reverseOrder());
        for (String seat : availableSeats) {
            int weight = seatWeights.getOrDefault(seat, 0);
            groupedSeats.computeIfAbsent(weight, k -> new ArrayList<>()).add(seat);
        }

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
                log.info("@@@ 티켓팅 ID = {} 좌석 예약 완료: Seat={}", ticketingId, seat);
                count++;
            }
        }

        log.info("1초에 {}개의 좌석 예약 완료.", count);
    }
}
