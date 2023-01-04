package antifraud.repository;

import antifraud.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@EnableJpaRepositories
public interface UserRepository extends JpaRepository<User, Long> {


    Optional<User> findUserEntityByUsernameIgnoreCase(String name);

    List<User> findAll();


}
