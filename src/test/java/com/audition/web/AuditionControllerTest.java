package com.audition.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.audition.model.AuditionComment;
import com.audition.model.AuditionPost;
import com.audition.service.AuditionService;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuditionControllerTest {

    @Autowired
    private transient TestRestTemplate restTemplate;

    @MockBean
    private transient AuditionService auditionService;

    @Test
    void shouldReturnAllPosts() {
        final List<AuditionPost> posts = List.of(
            new AuditionPost(1, 1, "title1", "body1", null),
            new AuditionPost(2, 2, "title2", "body2", null)
        );
        when(auditionService.getPosts()).thenReturn(posts);

        final ResponseEntity<AuditionPost[]> response = restTemplate.getForEntity("/posts", AuditionPost[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
    }

    @Test
    void shouldFilterPostsByUserId() {
        final List<AuditionPost> posts = List.of(
            new AuditionPost(1, 1, "title1", "body1", null),
            new AuditionPost(2, 2, "title2", "body2", null)
        );
        when(auditionService.getPosts()).thenReturn(posts);

        final String url = UriComponentsBuilder.fromPath("/posts")
            .queryParam("userId", 1)
            .toUriString();

        final ResponseEntity<AuditionPost[]> response = restTemplate.getForEntity(url, AuditionPost[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody()[0].getUserId()).isEqualTo(1);
    }

    @Test
    void shouldReturnPostByIdWithoutComments() {
        final AuditionPost post = new AuditionPost(1, 3, "title", "body", null);
        when(auditionService.getPostById("3")).thenReturn(post);

        final ResponseEntity<AuditionPost> response = restTemplate.getForEntity("/posts/3", AuditionPost.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getId()).isEqualTo(3);
    }

    @Test
    void shouldReturnPostWithCommentsWhenIncludeCommentsIsTrue() {
        final List<AuditionComment> comments = List.of(
            new AuditionComment(1, 5, "user", "email@test.com", "Nice!")
        );
        final AuditionPost post = new AuditionPost(2, 5, "title", "body", comments);

        when(auditionService.getPostWithComments("5")).thenReturn(post);

        final String url = UriComponentsBuilder.fromPath("/posts/5")
            .queryParam("includeComments", "true")
            .toUriString();

        final ResponseEntity<AuditionPost> response = restTemplate.getForEntity(url, AuditionPost.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getAuditionComments()).hasSize(1);
    }

    @Test
    void shouldReturnCommentsForPost() {
        final List<AuditionComment> comments = List.of(
            new AuditionComment(1, 6, "user", "email@test.com", "Nice post")
        );
        when(auditionService.getCommentsByPostId("6")).thenReturn(comments);

        final ResponseEntity<AuditionComment[]> response = restTemplate.getForEntity("/posts/6/comments",
            AuditionComment[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    void shouldReturnBadRequestForInvalidPostIdInPath() {
        final ResponseEntity<Map> response = restTemplate.getForEntity("/posts/abc", Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsKey("detail");
        assertThat(((String) response.getBody().get("detail")).toLowerCase(Locale.ROOT)).contains(
            "failed to convert 'id' with value: 'abc'");
    }

    @Test
    void shouldReturnBadRequestForInvalidCommentPostId() {
        final ResponseEntity<Map> response = restTemplate.getForEntity("/posts/foo/comments", Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(((String) response.getBody().get("detail")).toLowerCase(Locale.ROOT)).contains(
            "failed to convert 'id' with value: 'foo'");
    }

}
