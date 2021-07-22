package space.nov29.cataria.model;

import lombok.Data;

import javax.persistence.*;
import java.time.Instant;
import java.util.Set;

@Data
@Entity
@Table
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long id;

    @Column(nullable = false, updatable = false)
    private Instant createTime;

    @Column(nullable = false)
    private Instant lastEditTime;

    @Column
    private Instant publishedTime;

    @Column(nullable = false)
    private String title;

    @Column(unique = true)
    private String slug;

    @Column(nullable = false)
    private Boolean published;

    @ManyToOne(cascade = {CascadeType.PERSIST})
    private Category category;

    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinTable(
            name = "post_tag",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private Set<Tag> tags;

    @Lob
    @Column
    private String content;
}
