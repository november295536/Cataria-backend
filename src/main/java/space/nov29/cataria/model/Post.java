package space.nov29.cataria.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;

@Setter
@Getter
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

    @Column
    private String slug;

    @Column(nullable = false)
    private Boolean published;

    @Lob
    @Column
    private String content;
}
