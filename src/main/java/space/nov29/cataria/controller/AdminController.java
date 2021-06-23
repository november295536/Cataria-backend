package space.nov29.cataria.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import space.nov29.cataria.dto.*;
import space.nov29.cataria.model.Post;
import space.nov29.cataria.service.PostService;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private PostService postService;

    @GetMapping("/post/getAll")
    public PostListResponse showAllPost(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        Page<Post> posts = postService.getAllPosts(page, size);
        return new PostListResponse(posts);
    }

    @PostMapping("/post/create")
    public ResponseEntity createPost(@RequestBody PostDto postDto) {
        postService.createPost(postDto);
        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping("/post/update")
    public ResponseEntity updatePost(@RequestBody PostDto postDto) {
        postService.updatePost(postDto);
        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping("/post/delete")
    public ResponseEntity deletePost(@RequestBody DeletePostRequest deletePostRequest) {
        postService.deletePost(deletePostRequest.getId());
        return new ResponseEntity(HttpStatus.OK);
    }
    @GetMapping("/config/tags")
    public ResponseEntity<List<TagDto>> getTagList() {
        return new ResponseEntity<>(postService.getAllTags(), HttpStatus.OK);
    }

    @GetMapping("/config/categories")
    public ResponseEntity<List<CategoryDto>> getCategoryList() {
        return new ResponseEntity<>(postService.getAllCategory(), HttpStatus.OK);
    }
}
