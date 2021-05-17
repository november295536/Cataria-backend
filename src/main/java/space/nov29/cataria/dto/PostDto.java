package space.nov29.cataria.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Setter
@Getter
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
}
