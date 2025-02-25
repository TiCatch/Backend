package TiCatch.backend.global.service.redis;

import TiCatch.backend.domain.ticketing.entity.TicketingStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import static TiCatch.backend.global.constant.RedisConstants.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisService {

	private final RedisTemplate<String, String> redisTemplate;

	public void setValues(String key, String value) {
		redisTemplate.opsForValue().set(key, value);
		redisTemplate.expire(key, EXPIRE_TIMEOUT, TimeUnit.DAYS);
	}

	public void addToWaitingQueue(Long ticketId, String userId) {
		String queueKey = WAITING_QUEUE_PREFIX + ticketId;
		double score = System.currentTimeMillis();
		redisTemplate.opsForZSet().add(queueKey, userId, score);
	}

	public void addExpiryToControlQueue(Long ticketId, long ttl, TicketingStatus ticketingStatus) {
		redisTemplate.opsForValue().set(ticketingStatus + ":" + ticketId, TIME_TO_LIVE_PREFIX,  ttl, TimeUnit.SECONDS);
	}

	public void deleteWaitingQueue(Long ticketId) {
		String queueKey = WAITING_QUEUE_PREFIX + ticketId;
		redisTemplate.delete(queueKey);
	}

	public Long getWaitingQueueRank(Long ticketId, String userId) {
		String queueKey = WAITING_QUEUE_PREFIX + ticketId;
		Long rank = redisTemplate.opsForZSet().rank(queueKey, userId);
		if(rank == null) {
			return -1L;
		}
		return rank+1;
	}

	public Set<ZSetOperations.TypedTuple<String>> getBatchFromQueue(Long ticketId, int batchSize) {
		String queueKey = WAITING_QUEUE_PREFIX + ticketId;
		return redisTemplate.opsForZSet().rangeWithScores(queueKey, RANGE_START_INDEX, batchSize - 1);
	}

	public void removeBatchFromQueue(Long ticketId, int batchSize) {
		String queueKey = WAITING_QUEUE_PREFIX + ticketId;
		redisTemplate.opsForZSet().removeRange(queueKey, RANGE_START_INDEX, batchSize - 1);
	}

	public String getValues(String key) {
		ValueOperations<String, String> values = redisTemplate.opsForValue();
		return values.get(key);
	}

	public void deleteValues(String key) {
		redisTemplate.delete(key);
	}
}