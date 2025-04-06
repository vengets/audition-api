package com.audition.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Component
@ConfigurationProperties(prefix = "audition-integration-api")
public class AuditionIntegrationApiProperties {

    @Getter
    private transient String baseUrl;
    private transient String getPostsPath;
    private transient String getSinglePostPath;
    private transient String getPostCommentsPath;
    private transient String getCommentsByPost;

    public String getGetPostsUrl() {
        return baseUrl + getPostsPath;
    }

    public String getSinglePostUrl(final String postId) {
        return String.format(baseUrl + getSinglePostPath, postId);
    }

    public String getPostCommentsUrl(final String postId) {
        return String.format(baseUrl + getPostCommentsPath, postId);
    }

    public String getCommentsByPostUrl(final String postId) {
        return String.format(baseUrl + getCommentsByPost, postId);
    }

}
