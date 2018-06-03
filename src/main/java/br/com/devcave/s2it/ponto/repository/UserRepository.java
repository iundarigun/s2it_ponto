package br.com.devcave.s2it.ponto.repository;

import br.com.devcave.s2it.ponto.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByName(String name);

    User findByUsername(String username);

    boolean existsByUsername(String username);
}
