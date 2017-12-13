package com.rest.service;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class JwtSecurityRestExampleTest {
    private HttpServer server;
    private WebTarget target;
    private String token;
 
    @Before
    public void setUp() throws Exception {
        server = ServerUtil.startServer("com.rest.service");
 
        Client c = ClientBuilder.newClient();
        target = c.target(ServerUtil.BASE_URI);
    }
 
    @After
    public void tearDown() throws Exception {
        server.shutdown();
    }

    @Test
    public void testStatus() {
//        String responseMsg = target.path("security/status").request().get(String.class);
//        assertNotNull(responseMsg);
//        System.out.println(responseMsg);
    }
    
    @Test
    public void testHello() {
        String responseMsg = target.path("security/users/emel")
        		.queryParam("salute", "selam")
        		.request().get(String.class);
        assertEquals(responseMsg, "selam emel");
        System.out.println(responseMsg);
    }
    
    @Test
    public void testHelloDefault() {
        String responseMsg = target.path("security/users/emel")
        		.request().get(String.class);
        assertEquals(responseMsg, "Hello emel");
        System.out.println(responseMsg);
    }
    
    @Test
    public void testAuth() {
        token = target.path("security/authenticate")
        		.request()
        		.header("username", "admin005")
        		.header("password", "passs...")
        		.get(String.class);
        assertNotNull(token);
        System.out.println(token);
    }
    
    @Test
    public void testShowItems() {
    	String token = target.path("security/authenticate")
        		.request()
        		.header("username", "admin005")
        		.header("password", "passs...")
        		.get(String.class);
        assertNotNull(token);
        String response = target.path("security/showallitems")
        		.request()
        		.header("token", token)
        		.get(String.class);
        assertNotNull(response);
        System.out.println(response);
    }
}
