package space.nov29.cataria.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import space.nov29.cataria.dto.AuthenticationResponse;
import space.nov29.cataria.dto.LoginRequest;
import space.nov29.cataria.model.Post;
import space.nov29.cataria.model.Tag;
import space.nov29.cataria.repository.PostRepository;
import space.nov29.cataria.repository.TagRepository;
import space.nov29.cataria.service.AuthService;

import java.time.Instant;

@RestController
@Slf4j
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final TagRepository tagRepository;
    private final PostRepository postRepository;

    public AuthController(AuthService authService, TagRepository tagRepository, PostRepository postRepository) {
        this.authService = authService;
        this.tagRepository = tagRepository;
        this.postRepository = postRepository;
    }


//    @PostMapping("/signup")
//    public ResponseEntity signup(@RequestBody RegisterRequest registerRequest) {
//        authService.signup(registerRequest);
//        return new ResponseEntity(HttpStatus.OK);
//    }

    @PostMapping("/login")
    public AuthenticationResponse login(@RequestBody LoginRequest loginRequest) {
        return authService.login(loginRequest);
    }

    @GetMapping("/hello")
    public String hello() {
        Post post = new Post();
        post.setTitle("144");
        post.setPublished(false);
        post.setCreateTime(Instant.now());
        post.setLastEditTime(Instant.now());
        Tag tag = new Tag("tag789");
        tag.addPostToPostList(post);
        postRepository.save(post);

        log.warn("123456789");
        Tag savedTag = tagRepository.findByName("tag789").orElse(null);
        if(savedTag == null) return "is null";
        return String.format("size: %s", savedTag.getPosts().size());
    }

    @GetMapping("/hello2")
    public String hello2() {
        log.warn("123456789");
        Tag savedTag = tagRepository.findByName("tag789").orElse(null);
        if(savedTag == null) return "is null";
        return String.format("size: %s", savedTag.getPosts().size());
    }
}
