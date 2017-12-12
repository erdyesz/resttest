package com.rest.service.filter;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.ext.Provider;

/**
 * filters are primarily intended to manipulate request and response parameters like HTTP headers, URIs and/or HTTP methods.
 * 
 * post-matching request filters are invoked when a particular resource method has already been selected, 
 * such filters can not influence the resource method matching process. To overcome the above described limitation, 
 * there is a possibility to mark a server request filter as a pre-matching filter, i.e. to annotate the filter class with the @PreMatching annotation. 
 * Pre-matching filters are request filters that are executed before the request matching is started. 
 * Thanks to this, pre-matching request filters have the possibility to influence which method will be matched.
 * 
 * @author erdoganyesil
 */
@PreMatching
@Provider
public class PreMatchingFilter implements ContainerRequestFilter {

	@Override
	public void filter(ContainerRequestContext requestContext)
			throws IOException {
		// change all PUT methods to POST
		if (requestContext.getMethod().equals("PUT")) {
			requestContext.setMethod("POST");
		}
	}
}