package TiCatch.backend.domain.user.repository;


import TiCatch.backend.domain.user.entity.Credential;
import TiCatch.backend.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByUserId(Long userId);

	Optional<User> findByCredential(Credential credential);
}
