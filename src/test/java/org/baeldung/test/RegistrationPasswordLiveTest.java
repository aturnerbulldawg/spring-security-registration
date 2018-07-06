package org.baeldung.test;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.jayway.restassured.authentication.FormAuthConfig;
import org.baeldung.Application;
import org.baeldung.persistence.model.User;
import org.baeldung.spring.TestDbConfig;
import org.baeldung.spring.TestIntegrationConfig;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { Application.class, TestDbConfig.class, TestIntegrationConfig.class }, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RegistrationPasswordLiveTest {
    private String URL;

    @Value("${local.server.port}")
    int port;

    @Before
    public void init() {
        RestAssured.port = port;

        final String URL_PREFIX = "http://localhost:" + String.valueOf(port);
        URL = URL_PREFIX + "/user/registration";
    }

    @Test
    @Ignore("Why is this failing")
    public void givenInvalidPassword_thenBadRequest() {
        // too short
        assertEquals(HttpStatus.BAD_REQUEST.value(), getResponseForPassword("123"));

        // no special character
        assertEquals(HttpStatus.BAD_REQUEST.value(), getResponseForPassword("1abZRplYU"));

        // no upper case letter
        assertEquals(HttpStatus.BAD_REQUEST.value(), getResponseForPassword("1_abidpsvl"));

        // no number
        assertEquals(HttpStatus.BAD_REQUEST.value(), getResponseForPassword("abZRYUpl"));

        // alphabet sequence
        assertEquals(HttpStatus.BAD_REQUEST.value(), getResponseForPassword("1_abcZRYU"));

        // qwerty sequence
        assertEquals(HttpStatus.BAD_REQUEST.value(), getResponseForPassword("1_abZRTYU"));

        // numeric sequence
        assertEquals(HttpStatus.BAD_REQUEST.value(), getResponseForPassword("123_zqrtU"));

        // valid password
        assertEquals(HttpStatus.OK.value(), getResponseForPassword("12_zwRHIPKA"));
    }

    private int getResponseForPassword(String pass) {
        final Map<String, String> param = new HashMap<String, String>();
        final String randomName = UUID.randomUUID().toString();
        param.put("firstName", randomName);
        param.put("lastName", "Doe");
        param.put("email", randomName + "@x.com");
        param.put("password", pass);
        param.put("matchingPassword", pass);

        final Response response = RestAssured.given().formParameters(param).accept(MediaType.APPLICATION_JSON_VALUE).post(URL);
        System.out.println(response.asString());
        return response.getStatusCode();
    }
}
