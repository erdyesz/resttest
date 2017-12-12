package com.rest.service.interceptor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.ReaderInterceptor;
import javax.ws.rs.ext.ReaderInterceptorContext;

import com.rest.util.StreamUtil;


/**
 * Interceptors (implementations of ReaderInterceptor / WriterInterceptor) are executed only if request/response entity is available. 
 * In your case this means that only WriterInterceptor is being executed since you're sending entity (an instance of FooObj) 
 * to the client from your resource method. If you had a POST method that receives an input from user your ReaderInterceptor would be invoked as well.
 * 
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
 * interceptor will be executed only when resource methods
 * annotated with @Dummy annotation will be executed
 */
@Dummy
@Provider
public class DummyReaderInterceptor implements ReaderInterceptor {
	 
    @Override
    public Object aroundReadFrom(ReaderInterceptorContext context)
                    throws IOException, WebApplicationException {
    	
    	final InputStream inputStream = context.getInputStream();
    	String message = StreamUtil.getStringFromInputStream(inputStream) + ":#@appendstr";
    	context.setInputStream(new ByteArrayInputStream(message.getBytes(StandardCharsets.UTF_8.name())));
        return context.proceed();
    }
}
