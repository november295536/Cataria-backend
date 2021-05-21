package space.nov29.cataria.dto;

import lombok.Data;
import space.nov29.cataria.model.Post;
import space.nov29.cataria.model.Tag;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class TagDto {
    private Long id;
    private String name;
    private List<Long> postIds;

    public TagDto(Tag tag){
        this.id = tag.getId();
        this.name = tag.getName();
        this.postIds = tag.getPosts().stream().map(Post::getId).collect(Collectors.toList());
    }
}
