package TiCatch.backend.domain.user.entity;

import TiCatch.backend.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "credential")
public class Credential extends BaseTimeEntity {

	@Id
	@Column(length = 128)
	private String credentialId;

	@Column(length = 128)
	private String email;

	@Column(length = 10)
	private String credentialSocialPlatform;

	@NotNull
	@Enumerated(EnumType.STRING)
	private CredentialRole credentialRole;

}
