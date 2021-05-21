package space.nov29.cataria.dto;

import lombok.Data;
import space.nov29.cataria.model.Category;
import space.nov29.cataria.model.Post;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class CategoryDto {
    private Long id;
    private String name;
    private List<Long> postIds;

    public CategoryDto(Category category) {
        this.id = category.getId();
        this.name = category.getName();
        this.postIds = category.getPosts().stream().map((Post::getId)).collect(Collectors.toList());
    }
}
