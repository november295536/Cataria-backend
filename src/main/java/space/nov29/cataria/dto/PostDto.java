package space.nov29.cataria.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import space.nov29.cataria.model.Post;
import space.nov29.cataria.model.Tag;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class PostDto {
    private Long id;
    private String title;
    private String slug;
    private String content;
    private Boolean published;
    private Instant lastEditTime;
    private Instant createTime;
    private Instant publishedTime;
    private String category;
    private List<String> tags;

    public PostDto(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.slug = post.getSlug();
        this.content = post.getContent();
        this.published = post.getPublished();
        this.lastEditTime = post.getLastEditTime();
        this.createTime = post.getCreateTime();
        this.publishedTime = post.getPublishedTime();
        if (post.getCategory() == null) this.category = null;
        this.category = post.getCategory() != null ? post.getCategory().getName() : null;
        this.tags = new ArrayList<>(post.getTags()).
                stream()
                .map(Tag::getName)
                .collect(Collectors.toList());
    }
}
