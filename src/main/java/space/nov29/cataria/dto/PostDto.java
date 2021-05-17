package space.nov29.cataria.dto;

import lombok.Data;
import space.nov29.cataria.model.Post;
import space.nov29.cataria.model.Tag;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
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
        // 這邊會造成在update的時候若沒有指定 slug ，就會把 id 當成 slug 寫入 database
        String slug = post.getSlug();
        this.slug = Objects.requireNonNullElseGet(slug, () -> Long.toString(post.getId()));

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
