package space.nov29.cataria.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import space.nov29.cataria.dto.PostDto;
import space.nov29.cataria.dto.PostListResponse;
import space.nov29.cataria.exception.PostNotFoundException;
import space.nov29.cataria.model.Post;
import space.nov29.cataria.service.PostService;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @GetMapping
    public PostListResponse getPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        Page<Post> posts = postService.getPostsWithPagination(page, size);
        return new PostListResponse(posts);
    }

    @GetMapping("/{slug}")
    public ResponseEntity<PostDto> getPost(@PathVariable @RequestBody String slug){
        try {
            return new ResponseEntity<>(postService.getPost(slug), HttpStatus.OK);
        } catch (PostNotFoundException e) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }

}
