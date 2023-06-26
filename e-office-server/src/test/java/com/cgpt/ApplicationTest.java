package com.cgpt;

import com.cgpt.pojo.AdminLogin;
import com.cgpt.pojo.RespBean;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.util.UriComponentsBuilder;


import static org.junit.Assert.assertTrue;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApplicationTest {
    @Autowired
    private TestRestTemplate restTemplate;


    @Before
    public void getToken(){

    }



    @Test
    public void getOthers() {
        AntPathMatcher matcher = new AntPathMatcher();
        String pattern = matcher.extractPathWithinPattern("/users/{userId}", "/users/123"); // "{userId}"

    }


}
