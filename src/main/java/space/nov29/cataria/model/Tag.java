package space.nov29.cataria.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table
@Data
@NoArgsConstructor
@EqualsAndHashCode(exclude = "posts")
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @ManyToMany(mappedBy = "tags")
    private List<Post> posts;

    public Tag(String tagName) {
        this.name = tagName;
    }

    public void addPostToPostList(Post post) {
        if(posts == null) posts = new ArrayList<>();
        if(posts.contains(post)) return;

        posts.add(post);
        if(post.getTags() != null) {
            post.getTags().add(this);
            return;
        }
        Set<Tag> tags = new HashSet<>();
        tags.add(this);
        post.setTags(tags);
    }

    public void removePostFromPostList(Post post) {
        if(posts != null) posts.remove(post);
        if(post.getTags() == null) return;
        post.getTags().remove(this);
    }
}
