package TiCatch.backend.domain.ticketing.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class MakeSeatWeight {

    private final RedisTemplate<String, String> redisTemplate;

    @PostConstruct
    public void generateSeatWeights() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();

            InputStream weightStream = new ClassPathResource("seat_weights.json").getInputStream();
            Map<String, Integer> seatWeights = objectMapper.readValue(weightStream, new TypeReference<>() {});

            for (Map.Entry<String, Integer> entry : seatWeights.entrySet()) {
                String seatKey = entry.getKey();
                int weight = entry.getValue();
                redisTemplate.opsForHash().put("seat_score", seatKey, String.valueOf(weight));
            }

            System.out.println("Redis에 좌석 가중치 저장 완료!");

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Redis에 좌석별 가중치 저장 실패!");
        }
    }
}
