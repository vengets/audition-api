package com.audition.integration;

import com.audition.common.exception.SystemException;
import com.audition.configuration.AuditionIntegrationApiProperties;
import com.audition.model.AuditionComment;
import com.audition.model.AuditionPost;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class AuditionIntegrationClient {

    private static final Logger LOG = LoggerFactory.getLogger(AuditionIntegrationClient.class);
    @Autowired
    private transient RestTemplate restTemplate;

    @Autowired
    private transient AuditionIntegrationApiProperties apiProperties;

    public AuditionIntegrationClient(final RestTemplate restTemplate,
        final AuditionIntegrationApiProperties apiProperties) {
        this.restTemplate = restTemplate;
        this.apiProperties = apiProperties;
    }

    public List<AuditionPost> getPosts() {
        try {
            final ResponseEntity<AuditionPost[]> response = restTemplate.getForEntity(
                apiProperties.getGetPostsUrl(),
                AuditionPost[].class
            );

            return Arrays.asList(Objects.requireNonNull(response.getBody()));

        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            if (LOG.isErrorEnabled()) {
                LOG.error("Error while fetching posts: {} - {}", ex.getStatusCode(), ex.getResponseBodyAsString());
            }
            return Collections.emptyList();

        } catch (RestClientException ex) {
            if (LOG.isErrorEnabled()) {
                LOG.error("Client error during API call: {}", ex.getMessage(), ex);
            }
            return Collections.emptyList();
        }
    }


    public AuditionPost getPostById(final String id) {
        final String url = apiProperties.getSinglePostUrl(id);
        return getForObject(url, AuditionPost.class, "getPostById(" + id + ")");
    }

    public AuditionPost getPostWithComments(final String postId) {
        final AuditionPost post = getPostById(postId);
        final String commentsUrl = apiProperties.getPostCommentsUrl(postId);

        final AuditionComment[] comments = getForObject(commentsUrl, AuditionComment[].class,
            "getPostWithComments(" + postId + ")");
        post.setAuditionComments(Arrays.asList(comments));

        return post;
    }

    public List<AuditionComment> getCommentsByPostId(final String postId) {
        final String url = apiProperties.getCommentsByPostUrl(postId);
        final AuditionComment[] comments = getForObject(url, AuditionComment[].class,
            "getCommentsByPostId(" + postId + ")");
        return Arrays.asList(comments);
    }

    private <T> T getForObject(final String url, final Class<T> responseType, final String errorContext) {
        try {
            return Objects.requireNonNull(restTemplate.getForObject(url, responseType));
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new SystemException("Resource not found during: " + errorContext + e.getMessage(),
                    "Not Found " + e.getStatusCode().value(),
                    404, e);
            } else {
                throw new SystemException(
                    "Error during: " + errorContext + " - " + e.getMessage(),
                    e.getStatusCode().value(),
                    e
                );
            }
        }
    }
}
