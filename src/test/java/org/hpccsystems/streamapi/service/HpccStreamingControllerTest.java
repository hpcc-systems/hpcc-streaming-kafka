package org.hpccsystems.streamapi.service;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.hpccsystems.streamapi.service.dao.MessageDao;
import org.hpccsystems.streamapi.service.dao.TestMessageDaoStub;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

public class HpccStreamingControllerTest {

    private static final String BASE_URI = "http://localhost:8080/hpcc/stream";
    
    @Autowired
    private HpccStreamingController controller;
    
    @BeforeClass
    public static void beforeTests() {
        final SpringApplication app = new SpringApplication(HpccStreamingControllerTest.TestConfig.class);
        app.setShowBanner(false);
        app.run();
    }
  
    
    @Test(expected=RestClientException.class)
    public void must_produce_ok() {
        final RestTemplate restTemplate = new RestTemplate();
        
        final List<String> request = new ArrayList<String>();
        request.add("data1");
        
        restTemplate.postForEntity(BASE_URI, request, HpccProducerResponse.class);
    }

    @Test(expected=RestClientException.class)
    public void must_consume_ok() throws RestClientException {
        
        final RestTemplate restTemplate = new RestTemplate();
        
        final String resourceUri = BASE_URI + "/hpcc";
        
        final ResponseEntity<HpccConsumerResponse> response = restTemplate.getForEntity(resourceUri, HpccConsumerResponse.class);
        
        assertThat(response.getStatusCode(), equalTo(HttpStatus.NOT_FOUND));
//        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
//        assertThat(response.getBody().getId().getRel(), equalTo(Link.REL_SELF));
        
        System.out.println(response);

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

