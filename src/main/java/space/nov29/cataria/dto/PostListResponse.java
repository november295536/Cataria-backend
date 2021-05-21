package space.nov29.cataria.dto;

import lombok.Data;
import org.springframework.data.domain.Page;
import space.nov29.cataria.model.Post;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class PostListResponse {
    private List<PostDto> posts;
    private int currentPage;
    private int totalPage;
    private long totalItems;

    public PostListResponse(Page<Post> posts) {
        this.posts = posts.getContent().stream().map(PostDto::new).collect(Collectors.toList());
        this.currentPage = posts.getNumber();
        this.totalPage = posts.getTotalPages();
        this.totalItems = posts.getTotalElements();
    }
}
