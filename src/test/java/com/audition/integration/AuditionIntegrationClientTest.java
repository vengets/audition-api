package com.audition.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.audition.common.exception.SystemException;
import com.audition.configuration.AuditionIntegrationApiProperties;
import com.audition.model.AuditionComment;
import com.audition.model.AuditionPost;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

class AuditionIntegrationClientTest {

    private transient RestTemplate restTemplate;
    private transient AuditionIntegrationApiProperties apiProperties;
    private transient AuditionIntegrationClient client;

    @BeforeEach
    void setUp() {
        restTemplate = mock(RestTemplate.class);
        apiProperties = mock(AuditionIntegrationApiProperties.class);
        client = new AuditionIntegrationClient(restTemplate, apiProperties);
    }

    @Test
    void shouldFetchAllPostsSuccessfully() {
        final String url = "http://fake/posts";
        final AuditionPost[] posts = {new AuditionPost(), new AuditionPost()};
        final ResponseEntity<AuditionPost[]> responseEntity = new ResponseEntity<>(posts, HttpStatus.OK);

        when(apiProperties.getGetPostsUrl()).thenReturn(url);
        when(restTemplate.getForEntity(url, AuditionPost[].class)).thenReturn(responseEntity);

        final List<AuditionPost> result = client.getPosts();

        assertEquals(2, result.size());
    }

    @Test
    void shouldFetchSinglePostById() {
        final String id = "1";
        final String url = "http://fake/posts/1";
        final AuditionPost post = new AuditionPost();

        when(apiProperties.getSinglePostUrl(id)).thenReturn(url);
        when(restTemplate.getForObject(url, AuditionPost.class)).thenReturn(post);

        final AuditionPost result = client.getPostById(id);

        assertNotNull(result);
    }

    @Test
    void shouldThrowSystemExceptionFor404InGetPostById() {
        final String id = "999";
        final String url = "http://fake/posts/999";

        when(apiProperties.getSinglePostUrl(id)).thenReturn(url);
        when(restTemplate.getForObject(url, AuditionPost.class))
            .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND, "Not Found"));

        final SystemException ex = assertThrows(SystemException.class, () -> client.getPostById(id));
        assertEquals(404, ex.getStatusCode());
        assertTrue(ex.getMessage().contains("Resource not found during"));
    }

    @Test
    void shouldThrowSystemExceptionFor500InGetPostById() {
        final String id = "fail";
        final String url = "http://fake/posts/fail";

        when(apiProperties.getSinglePostUrl(id)).thenReturn(url);
        when(restTemplate.getForObject(url, AuditionPost.class))
            .thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Boom"));

        final SystemException ex = assertThrows(SystemException.class, () -> client.getPostById(id));
        assertEquals(500, ex.getStatusCode());
        assertTrue(ex.getMessage().contains("Error during"));
    }

    @Test
    void shouldGetPostWithComments() {
        final String postId = "5";
        final String postUrl = "http://fake/posts/5";
        final String commentsUrl = "http://fake/posts/5/comments";

        final AuditionPost post = new AuditionPost();
        final AuditionComment[] comments = {new AuditionComment(), new AuditionComment()};

        when(apiProperties.getSinglePostUrl(postId)).thenReturn(postUrl);
        when(apiProperties.getPostCommentsUrl(postId)).thenReturn(commentsUrl);
        when(restTemplate.getForObject(postUrl, AuditionPost.class)).thenReturn(post);
        when(restTemplate.getForObject(commentsUrl, AuditionComment[].class)).thenReturn(comments);

        final AuditionPost result = client.getPostWithComments(postId);

        assertNotNull(result);
        assertEquals(2, result.getAuditionComments().size());
    }

    @Test
    void shouldGetCommentsByPostId() {
        final String postId = "7";
        final String url = "http://fake/comments?postId=7";
        final AuditionComment[] comments = {new AuditionComment()};

        when(apiProperties.getCommentsByPostUrl(postId)).thenReturn(url);
        when(restTemplate.getForObject(url, AuditionComment[].class)).thenReturn(comments);

        final List<AuditionComment> result = client.getCommentsByPostId(postId);

        assertEquals(1, result.size());
    }
}
