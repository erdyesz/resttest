package com.avaldes.service;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

/**
 * Filters can be used when you want to modify any request or response parameters like headers. 
 * For example you would like to add a response header "X-Powered-By" to each generated response. 
 * Instead of adding this header in each resource method you would use a response filter to add this header.
 * 
 * The filter must inherit from the ContainerResponseFilter and must be registered as a provider. 
 * The filter will be executed for every response which is in most cases after the resource method is executed. 
 * Response filters are executed even if the resource method is not run, 
 * for example when the resource method is not found and 404 "Not found" response code is returned by the Jersey runtime. 
 * In this case the filter will be executed and will process the 404 response.
 * 
 * All the request filters shown above was implemented as post-matching filters. 
 * It means that the filters would be applied only after a suitable resource method has been selected to process the actual request i.e. 
 * after request matching happens. Request matching is the process of finding a resource method 
 * that should be executed based on the request path and other request parameters. Since post-matching request filters are invoked
 * when a particular resource method has already been selected, such filters can not influence the resource method matching process.
 * 
 * @author erdoganyesil
 *
 */
@Provider
public class LoggingFilter implements ContainerRequestFilter,ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        //DO Stuff
    	//System.out.println(requestContext.getHeaderString("username"));
    	//System.out.println(requestContext.getHeaderString("token"));
    	// final SecurityContext securityContext = requestContext.getSecurityContext();
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
            throws IOException {
    	//System.out.println("2222222sdsdsfdsfdsfdsfds sfdsfdsf");
    	//System.out.println("POST RESPONSE::=>" + StreamUtil.getStringFromInputStream(responseContext.getEntityStream()));
    }
}
