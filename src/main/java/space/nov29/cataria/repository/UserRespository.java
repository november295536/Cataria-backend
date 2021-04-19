package space.nov29.cataria.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import space.nov29.cataria.model.User;

import java.util.Optional;

@Repository
public interface UserRespository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
}
