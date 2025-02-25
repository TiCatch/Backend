package TiCatch.backend.domain.auth.service;

import TiCatch.backend.domain.auth.dto.TokenDto;
import TiCatch.backend.domain.auth.dto.kakao.KakaoAccountDto;
import TiCatch.backend.domain.auth.dto.kakao.KakaoTokenDto;
import TiCatch.backend.domain.auth.dto.response.LoginResponseDto;
import TiCatch.backend.domain.auth.dto.response.UserResDto;
import TiCatch.backend.domain.auth.util.JwtProvider;
import TiCatch.backend.domain.user.entity.Credential;
import TiCatch.backend.domain.user.entity.CredentialRole;
import TiCatch.backend.domain.user.repository.CredentialRepository;
import TiCatch.backend.domain.user.repository.UserRepository;
import TiCatch.backend.global.exception.UnAuthorizedAccessException;
import TiCatch.backend.global.service.redis.RedisService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import TiCatch.backend.domain.user.entity.User;
import TiCatch.backend.global.exception.NotExistUserException;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class KakaoAuthService {

	@Value("${spring.security.oauth2.client.registration.kakao.client-id}")
	private String KAKAO_CLIENT_ID;
	@Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
	private String KAKAO_CLIENT_SECRET;
	@Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
	private String KAKAO_REDIRECT_URI;
	@Value("${spring.security.oauth2.client.provider.kakao.token-uri}")
	private String KAKAO_TOKEN_URI;
	@Value("${spring.security.oauth2.client.provider.kakao.user-info-uri}")
	private String KAKAO_USER_INFO_URI;
	@Value("${spring.security.oauth2.client.registration.kakao.authorization-grant-type}")
	private String KAKAO_GRANT_TYPE;

	@Autowired
	private final RedisService redisService;
	private final JwtProvider jwtProvider;
	private final CredentialRepository credentialRepository;
	private final UserRepository userRepository;
	private TokenDto tokenDto;

	@Transactional
	public LoginResponseDto kakaoLogin(String code) {
		KakaoTokenDto kakaoTokenDto = getKakaoAccessToken(code);
		User user = getKakaoUserInfo(kakaoTokenDto);
		User existUser = userRepository.findByUserId(user.getUserId()).orElse(null);

		if (existUser == null) {
			log.info("존재하지 않는 회원정보입니다. 새로 저장합니다.");
			userRepository.save(user);
			log.info("member_id = {}", user.getUserId());
		}

		Long userId = user.getUserId();
		user = userRepository.findByUserId(userId).orElseThrow(NotExistUserException::new);
		Credential credential = credentialRepository.findByCredentialId(
			user.getCredential().getCredentialId()).orElseThrow(NotExistUserException::new);

		TokenDto tokenDto = jwtProvider.generateTokenDto(user.getCredential().getEmail());
		log.info("[login] 계정 확인 완료! " + user.getUserNickname() + "님 로그인 성공!");
		redisService.setValues(tokenDto.getRefreshToken(), user.getCredential().getEmail());

		return LoginResponseDto.builder()
			.tokenDto(tokenDto)
			.userResDto(UserResDto.builder()
				.userId(user.getUserId())
				.userNickname(user.getUserNickname())
				.userScore(user.getUserScore())
				.createdDate(user.getCreatedDate())
				.modifiedDate(user.getModifiedDate())
				.tokenDto(tokenDto)
				.credentialId(credential.getCredentialId())
				.userEmail(credential.getEmail())
				.build())
			.build();
	}

	private KakaoTokenDto getKakaoAccessToken(String code) {

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		headers.add("Accept", "application/json");

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", KAKAO_GRANT_TYPE);
		params.add("client_id", KAKAO_CLIENT_ID);
		params.add("redirect_uri", KAKAO_REDIRECT_URI);
		params.add("code", code);
		params.add("client_secret", KAKAO_CLIENT_SECRET);


		log.info("https.proxyHost: {}", System.getProperty("https.proxyHost"));
		log.info("https.proxyPort: {}", System.getProperty("https.proxyPort"));
		HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, headers);

		RestTemplate restTemplate = new RestTemplate();
		restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
		ResponseEntity<String> accessTokenResponse =
			restTemplate.postForEntity(KAKAO_TOKEN_URI, kakaoTokenRequest, String.class);

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		try {
			return objectMapper.readValue(accessTokenResponse.getBody(), KakaoTokenDto.class);
		} catch (JsonProcessingException e) {
			throw new RuntimeException();
		}
	}

//	public KakaoTokenDto getKakaoAccessToken(String code) {
//		log.info("///////////////// KAKAO_TOKEN_URI : {}", KAKAO_TOKEN_URI);
//
//		try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
//			HttpPost httpPost = new HttpPost(KAKAO_TOKEN_URI);
//
//			// 헤더 설정
//			httpPost.setHeader("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE);
//			httpPost.setHeader("Accept", "application/json");
//
//			// 요청 바디 설정 (URL 인코딩된 폼 데이터)
//			MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
//			params.add("grant_type", KAKAO_GRANT_TYPE);
//			params.add("client_id", KAKAO_CLIENT_ID);
//			params.add("redirect_uri", KAKAO_REDIRECT_URI);
//			params.add("code", code);
//			params.add("client_secret", KAKAO_CLIENT_SECRET);
//
//			String formData = params.entrySet().stream()
//					.flatMap(entry -> entry.getValue().stream().map(value -> entry.getKey() + "=" + value))
//					.collect(Collectors.joining("&"));
//
//			httpPost.setEntity(new StringEntity(formData, StandardCharsets.UTF_8));
//
//			// HTTP 요청 보내기
//			try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
//				String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
//				log.info("Kakao Token Response: {}", responseBody);
//
//				// JSON 파싱
//				ObjectMapper objectMapper = new ObjectMapper();
//				objectMapper.registerModule(new JavaTimeModule());
//				objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//
//				return objectMapper.readValue(responseBody, KakaoTokenDto.class);
//			} catch (ParseException e) {
//                throw new RuntimeException(e);
//            }
//        } catch (IOException e) {
//			throw new RuntimeException("카카오 액세스 토큰 요청 중 오류 발생", e);
//		}
//	}

	private User getKakaoUserInfo(KakaoTokenDto kakaoTokenDto) {
		RestTemplate restTemplate = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + kakaoTokenDto.getAccessToken());

		HttpEntity<MultiValueMap<String, String>> accountInfoRequest = new HttpEntity<>(headers);

		ResponseEntity<String> accountInfoResponse = restTemplate.exchange(
			KAKAO_USER_INFO_URI,
			HttpMethod.POST,
			accountInfoRequest,
			String.class
		);

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		KakaoAccountDto kakaoAccountDto = null;

		try {
			kakaoAccountDto = objectMapper.readValue(accountInfoResponse.getBody(), KakaoAccountDto.class);
		} catch (JsonProcessingException e) {
			log.error(e.toString());
		}

		Map<String, Object> kakaoAccount = kakaoAccountDto.getKakao_account();
		Map<String, Object> kakaoProfile = (Map<String, Object>)kakaoAccount.get("profile");

		String email = (String)kakaoAccount.get("email");
		String nickname = (String)kakaoProfile.get("nickname");

		tokenDto = jwtProvider.generateTokenDto(email);
		Credential credential = credentialRepository.findByEmail(email).orElse(null);

		if (credential != null) {
			log.info("이미 존재하는 email입니다. 바로 유저 정보를 반환합니다.");
			return userRepository.findByCredential(credential).orElse(null);
		}

		credential = Credential.builder()
			.email(email)
			.credentialId(UUID.randomUUID().toString())
			.credentialRole(CredentialRole.USER)
			.credentialSocialPlatform("kakao")
			.build();

		credentialRepository.save(credential);

		return User.builder()
			.credential(credential)
			.userNickname(nickname)
			.userScore(0)
			.build();
	}

	public TokenDto reissueAccessToken(String refreshToken) {
		String email = redisService.getValues(refreshToken);
		if (email == null) {
			throw new UnAuthorizedAccessException();
		}
		TokenDto tokenDto = jwtProvider.generateTokenDto(email);
		return tokenDto;
	}
}