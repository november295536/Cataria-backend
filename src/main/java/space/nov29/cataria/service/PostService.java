package space.nov29.cataria.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import space.nov29.cataria.dto.PostDto;
import space.nov29.cataria.exception.PostNotFoundException;
import space.nov29.cataria.model.Post;
import space.nov29.cataria.repository.PostRespository;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PostService {

    @Autowired
    private PostRespository postRespository;

    public void createPost(PostDto postDto) {
        Post post = new Post();
        post.setTitle(postDto.getTitle());
        post.setContent(postDto.getContent());
        post.setSlug(postDto.getSlug());
        post.setPublished(postDto.getPublished());
        Instant currentTime = Instant.now();
        post.setCreateTime(currentTime);
        post.setLastEditTime(currentTime);

        postRespository.save(post);

    }

    public void updatePost(PostDto postDto) {
        Post post = new Post();
    }

//    public List<PostDto> getPage(Integer page) {
//
//    }

    public PostDto getPost(String slug) {
        Post post = postRespository
                .findBySlug(slug)
                .orElse(
                        postRespository
                                .findById(Long.parseLong(slug))
                                .orElseThrow(()->new PostNotFoundException("slug: "+ slug)));

        return mapFromPostToDto(post);
    }

    public List<PostDto> getAllPosts() {
        List<Post> posts = postRespository.findAll();
        return posts.stream().map(this::mapFromPostToDto).collect(Collectors.toList());
    }

    private PostDto mapFromPostToDto(Post post) {
        PostDto dto = new PostDto();
        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        String slug = post.getSlug();
        dto.setSlug(Objects.requireNonNullElseGet(slug, () -> Long.toString(post.getId())));
        dto.setContent(post.getContent());
        dto.setLastEditTime(post.getLastEditTime());
        dto.setPublished(post.getPublished());

        return dto;
    }


}
