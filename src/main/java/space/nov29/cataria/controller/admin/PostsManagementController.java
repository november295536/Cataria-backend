package space.nov29.cataria.controller.admin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import space.nov29.cataria.dto.CategoryDto;
import space.nov29.cataria.dto.PostDto;
import space.nov29.cataria.dto.PostListResponse;
import space.nov29.cataria.dto.TagDto;
import space.nov29.cataria.model.Post;
import space.nov29.cataria.service.PostsService;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/admin")
public class PostsManagementController {

    @Autowired
    private PostsService postsService;

    @GetMapping("/posts")
    public PostListResponse showAllPost(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        Page<Post> posts = postsService.getAllPosts(page, size);
        return new PostListResponse(posts);
    }

    @PostMapping("/posts")
    public ResponseEntity createPost(@RequestBody PostDto postDto) {
        postsService.createPost(postDto);
        return new ResponseEntity(HttpStatus.OK);
    }

    @PutMapping("/posts")
    public ResponseEntity updatePost(@RequestBody PostDto postDto) {
        postsService.updatePost(postDto);
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/posts")
    public ResponseEntity deletePost(@RequestParam long id) {
        postsService.deletePost(id);
        return new ResponseEntity(HttpStatus.OK);
    }
    
    @GetMapping("/tags")
    public ResponseEntity<List<TagDto>> getTagList() {
        return new ResponseEntity<>(postsService.getAllTags(), HttpStatus.OK);
    }

    @GetMapping("/categories")
    public ResponseEntity<List<CategoryDto>> getCategoryList() {
        return new ResponseEntity<>(postsService.getAllCategory(), HttpStatus.OK);
    }
}
