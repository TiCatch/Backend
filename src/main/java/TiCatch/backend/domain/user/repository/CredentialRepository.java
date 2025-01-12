package TiCatch.backend.domain.user.repository;

import TiCatch.backend.domain.user.entity.Credential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CredentialRepository extends JpaRepository<Credential, Long> {
    Optional<Credential> findByEmail(String email);

    Optional<Credential> findByCredentialId(String credentialId);
}
