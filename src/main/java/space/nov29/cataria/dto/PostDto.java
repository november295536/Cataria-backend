package space.nov29.cataria.dto;

import java.time.Instant;

public class PostDto {
    private Long id;
    private String title;
    private String slug;
    private String content;
    private Boolean published;
    private Instant lastEditTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Boolean getPublished() {
        return published;
    }

    public void setPublished(Boolean published) {
        this.published = published;
    }

    public Instant getLastEditTime() {
        return lastEditTime;
    }

    public void setLastEditTime(Instant lastEditTime) {
        this.lastEditTime = lastEditTime;
    }
}
