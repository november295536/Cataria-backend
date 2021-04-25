package space.nov29.cataria.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import space.nov29.cataria.model.Post;

import java.util.Optional;

@Repository
public interface PostRespository extends JpaRepository<Post, Long> {
    Optional<Post> findBySlug(String slug);
}
