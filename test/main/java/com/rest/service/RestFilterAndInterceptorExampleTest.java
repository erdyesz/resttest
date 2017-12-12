package com.rest.service;


import static org.junit.Assert.assertEquals;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class RestFilterAndInterceptorExampleTest {
    private HttpServer server;
    private WebTarget target;
 
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
    public void testHello() {
    	Response output = target.path("test/users/emel")
        		.queryParam("salute", "selam")
        		.request().get();
        assertEquals(200, output.getStatus());
    }
    
    @Test
    public void testHelloError() {
    	Response output = target.path("test/users/emel")
        		.request().get();
        assertEquals(422, output.getStatus());
    }
}
