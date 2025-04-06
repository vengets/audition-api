package com.audition.service;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.audition.integration.AuditionIntegrationClient;
import com.audition.model.AuditionComment;
import com.audition.model.AuditionPost;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AuditionServiceTest {

    private transient AuditionIntegrationClient mockIntegrationClient;
    private transient AuditionService auditionService;

    @BeforeEach
    void setUp() {
        mockIntegrationClient = mock(AuditionIntegrationClient.class);
        auditionService = new AuditionService();

        try {
            final var field = AuditionService.class.getDeclaredField("auditionIntegrationClient");
            field.setAccessible(true);
            field.set(auditionService, mockIntegrationClient);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new IllegalStateException("Failed to inject mock client", e);
        }
    }

    @Test
    void shouldReturnPosts() {
        final List<AuditionPost> expectedPosts = List.of(new AuditionPost());
        when(mockIntegrationClient.getPosts()).thenReturn(expectedPosts);

        final List<AuditionPost> result = auditionService.getPosts();

        assertEquals(expectedPosts, result);
        verify(mockIntegrationClient).getPosts();
    }

    @Test
    void shouldReturnPostById() {
        final String id = "42";
        final AuditionPost post = new AuditionPost();
        when(mockIntegrationClient.getPostById(id)).thenReturn(post);

        final AuditionPost result = auditionService.getPostById(id);

        assertEquals(post, result);
        verify(mockIntegrationClient).getPostById(id);
    }

    @Test
    void shouldReturnPostWithComments() {
        final String postId = "101";
        final AuditionPost post = new AuditionPost();
        when(mockIntegrationClient.getPostWithComments(postId)).thenReturn(post);

        final AuditionPost result = auditionService.getPostWithComments(postId);

        assertEquals(post, result);
        verify(mockIntegrationClient).getPostWithComments(postId);
    }

    @Test
    void shouldReturnCommentsByPostId() {
        final String postId = "99";
        final List<AuditionComment> comments = singletonList(new AuditionComment());
        when(mockIntegrationClient.getCommentsByPostId(postId)).thenReturn(comments);

        final List<AuditionComment> result = auditionService.getCommentsByPostId(postId);

        assertEquals(comments, result);
        verify(mockIntegrationClient).getCommentsByPostId(postId);
    }
}
