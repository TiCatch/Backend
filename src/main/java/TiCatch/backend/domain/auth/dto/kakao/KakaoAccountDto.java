package TiCatch.backend.domain.auth.dto.kakao;

import lombok.Getter;

import java.util.Map;

@Getter
public class KakaoAccountDto {
  private String id; //회원 번호
  private String connected_at; // 연결된 시간
  private Map<String, Object> kakao_account; //카카오계정 정보
}
