package TiCatch.backend.global.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import TiCatch.backend.global.service.redis.RedisExpirationListener;

@Configuration
public class RedisConfig {

	@Value("${spring.data.redis.host}")
	private String redisHost;

	@Value("${spring.data.redis.port}")
	private String redisPort;

	@Value("${spring.data.redis.password}")
	private String redisPassword;

	@Bean
	@Primary  // 기본 Redis 연결 설정
	public RedisConnectionFactory redisConnectionFactory() {
		RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
		redisStandaloneConfiguration.setHostName(redisHost);
		redisStandaloneConfiguration.setPort(Integer.parseInt(redisPort));
		redisStandaloneConfiguration.setPassword(redisPassword);

		return new LettuceConnectionFactory(redisStandaloneConfiguration);
	}

	@Bean
	public ReactiveRedisConnectionFactory reactiveRedisConnectionFactory() {
		RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
		redisStandaloneConfiguration.setHostName(redisHost);
		redisStandaloneConfiguration.setPort(Integer.parseInt(redisPort));
		redisStandaloneConfiguration.setPassword(redisPassword);
		return new LettuceConnectionFactory(redisStandaloneConfiguration);
	}

	// RedisTemplate 설정 (동기 방식)
	@Bean
	@Qualifier("redisTemplate")
	public RedisTemplate<String, Object> redisTemplate() {
		RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(redisConnectionFactory());
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		redisTemplate.setValueSerializer(new StringRedisSerializer());
		return redisTemplate;
	}

	// ReactiveRedisTemplate 설정 (비동기 방식)
	@Bean
	@Qualifier("reactiveRedisTemplate")
	public ReactiveRedisTemplate<String, String> reactiveRedisTemplate(
			@Qualifier("reactiveRedisConnectionFactory") ReactiveRedisConnectionFactory factory) {
		RedisSerializationContext<String, String> serializationContext = RedisSerializationContext
				.<String, String>newSerializationContext(new StringRedisSerializer())
				.key(new StringRedisSerializer())
				.value(new StringRedisSerializer())
				.hashKey(new StringRedisSerializer())
				.hashValue(new StringRedisSerializer())
				.build();

		return new ReactiveRedisTemplate<>(factory, serializationContext);
	}

	// Key Expiry 이벤트 감지를 위한 RedisMessageListenerContainer 추가
	@Bean
	public RedisMessageListenerContainer redisMessageListenerContainer(
			RedisConnectionFactory connectionFactory,
			RedisExpirationListener redisExpirationListener) {

		RedisMessageListenerContainer container = new RedisMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.addMessageListener(new MessageListenerAdapter(redisExpirationListener),
				new PatternTopic("__keyevent@0__:expired")); // 0번 DB 기준

		return container;
	}
}