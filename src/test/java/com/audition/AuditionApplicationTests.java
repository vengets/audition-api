package com.audition;

import static org.assertj.core.api.Assertions.assertThat;

import com.audition.web.AuditionController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuditionApplicationTests {

    @Autowired
    private transient ApplicationContext applicationContext;

    @Autowired
    private transient AuditionController auditionController;

    @Test
    void contextLoads() {
        assertThat(applicationContext).isNotNull();
        assertThat(applicationContext.containsBean("auditionLogger")).isTrue();
    }

    @Test
    void auditionServiceShouldBeLoaded() {
        assertThat(auditionController).isNotNull();
    }


}
