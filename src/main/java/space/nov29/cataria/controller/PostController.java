package space.nov29.cataria.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import space.nov29.cataria.dto.PostDto;
import space.nov29.cataria.exception.PostNotFoundException;
import space.nov29.cataria.model.Post;
import space.nov29.cataria.service.PostService;

import java.util.List;

@RestController
@RequestMapping("/api/posts/")
public class PostController {

    @Autowired
    private PostService postService;

    @GetMapping("/getAll")
    public ResponseEntity<List<PostDto>> showAllPost() {
        return new ResponseEntity<>(postService.getAllPosts(), HttpStatus.OK);
    }

//    @GetMapping("/get/page/{page}")
//    public ResponseEntity<List<PostDto>> getPage(@PathVariable @RequestBody Integer page) {
//        //這邊之後用 redis 生成每篇文章的簡短暫存
//       return new ResponseEntity<>(postService.getPage(page), HttpStatus.OK);
//    }

    @GetMapping("/get/{slug}")
    public ResponseEntity<PostDto> getPost(@PathVariable @RequestBody String slug){
        try {
            return new ResponseEntity<>(postService.getPost(slug), HttpStatus.OK);
        } catch (PostNotFoundException e) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }

}
