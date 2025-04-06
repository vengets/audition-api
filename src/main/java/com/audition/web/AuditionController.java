package com.audition.web;

import com.audition.model.AuditionComment;
import com.audition.model.AuditionPost;
import com.audition.service.AuditionService;
import jakarta.validation.constraints.Positive;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("/posts")
public class AuditionController {

    @Autowired
    private transient AuditionService auditionService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<AuditionPost>> getPosts(
        @RequestParam(name = "userId", required = false) final Integer userId) {

        List<AuditionPost> posts = auditionService.getPosts();

        if (userId != null) {
            posts = posts.stream()
                .filter(post -> userId.equals(post.getUserId()))
                .collect(Collectors.toList());
        }

        return ResponseEntity.ok(posts);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AuditionPost> getPostById(
        @PathVariable("id") @Positive(message = "Invalid Post ID. Must be a positive number.") final Integer postId,
        @RequestParam(name = "includeComments", defaultValue = "false") final boolean includeComments) {

        final AuditionPost post = includeComments
            ? auditionService.getPostWithComments(postId.toString())
            : auditionService.getPostById(postId.toString());

        return ResponseEntity.ok(post);
    }

    @GetMapping(value = "/{id}/comments", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<AuditionComment>> getCommentsByPostId(@PathVariable("id")
    @Positive(message = "Invalid Post ID. Must be a positive number.") final Integer postId) {
        final List<AuditionComment> comments = auditionService.getCommentsByPostId(postId.toString());
        return ResponseEntity.ok(comments);
    }
}
