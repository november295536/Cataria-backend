package space.nov29.cataria.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import space.nov29.cataria.model.Post;

import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    Page<Post> findByPublishedTrueOrderByPublishedTimeDesc(Pageable pageable);
    Page<Post> findAll(Pageable pageable);
    Optional<Post> findBySlug(String slug);
}
