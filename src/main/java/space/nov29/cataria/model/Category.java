package space.nov29.cataria.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table
@Data
@NoArgsConstructor
@EqualsAndHashCode(exclude = "posts")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "category")
    private List<Post> posts;

    public Category(String categoryName) { this.name = categoryName; }

    public void addPostToPostList(Post post) {
        if(posts == null) posts = new ArrayList<>();
        if(posts.contains(post)) return;

        posts.add(post);
        post.setCategory(this);
    }

    public void removePostFromPostList(Post post) {
        if(posts == null) return;
        posts.remove(post);
        post.setCategory(null);
    }
}
