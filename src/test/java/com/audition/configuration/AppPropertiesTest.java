package com.audition.configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@EnableConfigurationProperties(AppProperties.class)
class AppPropertiesTest {

    @Autowired
    private transient AppProperties appProperties;

    @Test
    void shouldLoadProperties() {
        assertNotNull(appProperties);
        assertEquals("en_US", appProperties.getLocale());
        assertEquals("yyyy-MM-dd", appProperties.getDateFormat());
    }
}
