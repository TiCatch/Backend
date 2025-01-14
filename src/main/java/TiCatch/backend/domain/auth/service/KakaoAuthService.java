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

		log.info("[login] 계정 확인 완료! " + user.getUserNickname() + "님 로그인 성공!");
		log.info("grantType = {}", tokenDto.getGrantType());
		log.info("accessToken = {}", tokenDto.getAccessToken());
		log.info("refreshToken = {}", tokenDto.getRefreshToken());

		//refreshToken을 Redis에 저장
		redisService.setValues(credential.getEmail() + "_refreshToken", tokenDto.getRefreshToken());

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

		HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, headers);

		RestTemplate restTemplate = new RestTemplate();
		restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
		log.info("token-uri = {}", KAKAO_TOKEN_URI);

		ResponseEntity<String> accessTokenResponse =
			restTemplate.postForEntity(KAKAO_TOKEN_URI, kakaoTokenRequest, String.class);

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		KakaoTokenDto kakaoTokenDto = null;

//		try {
//			kakaoTokenDto = objectMapper.readValue(accessTokenResponse.getBody(), KakaoTokenDto.class);
//		} catch (JsonProcessingException e) {
//			log.error(e.toString());
//		}
		try {
			// 변경된 부분: setter 대신 전체 생성자를 활용한 매핑
			return objectMapper.readValue(accessTokenResponse.getBody(), KakaoTokenDto.class);
		} catch (JsonProcessingException e) {
			log.error("Error parsing KakaoTokenDto: {}", e.getMessage());
			throw new RuntimeException("Kakao token parsing failed", e); // 예외 처리 강화
		}

//		return kakaoTokenDto;
	}

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

}