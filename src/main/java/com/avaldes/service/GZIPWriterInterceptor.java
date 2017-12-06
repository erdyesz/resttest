package com.avaldes.service;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;

/**
 * Interceptors share a common API for the server and the client side. Whereas filters are primarily intended to manipulate request 
 * and response parameters like HTTP headers, URIs and/or HTTP methods, interceptors are intended to manipulate entities, 
 * via manipulating entity input/output streams. If you for example need to encode entity body of a client request 
 * then you could implement an interceptor to do the work for you.
 * 
 * There are two kinds of interceptors, ReaderInterceptor and WriterInterceptor. Reader interceptors are used to manipulate inbound entity streams. 
 * These are the streams coming from the "wire". So, using a reader interceptor you can manipulate request entity stream on the server side 
 * (where an entity is read from the client request) and response entity stream on the client side (where an entity is read from the server response). 
 * Writer interceptors are used for cases where entity is written to the "wire" which on the server means when writing out a response entity 
 * and on the client side when writing request entity for a request to be sent out to the server. Writer and reader interceptors 
 * are executed before message body readers or writers are executed and their primary intention is to wrap the entity streams t
 * hat will be used in message body reader and writers.
 * 
 * 
 * @author erdoganyesil
 *
 */
//interceptor will be executed only when resource methods
//annotated with @Compress annotation will be executed
//@Compress
//@Provider
public class GZIPWriterInterceptor implements WriterInterceptor {

	@Override
	public void aroundWriteTo(WriterInterceptorContext context) throws IOException, WebApplicationException {
		final OutputStream outputStream = context.getOutputStream();
		context.setOutputStream(new GZIPOutputStream(outputStream));
		context.proceed();
	}
}
