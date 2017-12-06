package com.avaldes.service;


import java.net.URI;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.core.UriBuilder;


public class ServerUtil {
	
    public static final URI BASE_URI = UriBuilder.fromUri("http://localhost").port(9765).build();
	
	/**
	 * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
	 * @return Grizzly HTTP server.
	 */
	public static HttpServer startServer(String packagePath) {
	    try {
			// create a resource config that scans for JAX-RS resources and providers
			// in $package package
			final ResourceConfig rc = new ResourceConfig().packages(packagePath);

			// create and start a new instance of grizzly http server
			// exposing the Jersey application at BASE_URI
			return GrizzlyHttpServerFactory.createHttpServer(BASE_URI, rc);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println(BASE_URI);
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

}
