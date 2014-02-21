package org.hpccsystems.streamapi.service;

import static com.jayway.restassured.RestAssured.get;
import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

import org.hpccsystems.streamapi.service.dao.MessageDao;
import org.hpccsystems.streamapi.service.dao.TestMessageDaoStub;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClientException;

import com.jayway.restassured.http.ContentType;

public class HpccStreamingControllerTest {

    @Autowired
    private HpccStreamingController controller;
    
    @BeforeClass
    public static void beforeTests() {
        final SpringApplication app = new SpringApplication(HpccStreamingControllerTest.TestConfig.class);
        app.setShowBanner(false);
        app.run();
    }
  
    
    @Ignore
    @Test
    public void must_produce_ok() {
        given()
            .contentType(ContentType.JSON)
            .body("{ topic: \"http\", key: \"0\", message: \"abc\"}")
        .then()
            .expect().body(equalTo("foo"))
        .when()
            .post("/hpccstream");
    }

    @Ignore
    @Test
    public void must_consume_ok() throws RestClientException {
        
        get("/hpccstream/http")
        .then()
            .assertThat().statusCode(equalTo(200));

    }

    @Configuration
    public static class TestConfig {

        @Bean
        public MessageDao messageDao() {
            return new TestMessageDaoStub();
        }
        
        @Bean
        public EmbeddedServletContainerFactory embeddedServletContainerFactory() {
            return new TomcatEmbeddedServletContainerFactory();
        }
    }
}

