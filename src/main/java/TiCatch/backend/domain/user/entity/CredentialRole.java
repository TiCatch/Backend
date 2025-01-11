package TiCatch.backend.domain.user.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum CredentialRole {
    USER("ROLE_USER", "사용자"),
    ADMIN("ROLE_ADMIN", "관리자"),
    GUEST("GUEST", "게스트");

    private final String code;
    private final String displayName;

    public static CredentialRole of(String code){
        return Arrays.stream(CredentialRole.values())
                .filter(r-> r.getCode().equals(code))
                .findAny()
                .orElse(GUEST);
    }
}