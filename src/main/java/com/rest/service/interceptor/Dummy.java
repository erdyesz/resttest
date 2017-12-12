package com.rest.service.interceptor;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.ws.rs.NameBinding;

/**
 * @Dummy annotation is the name binding annotation
 */
@NameBinding
@Retention(RetentionPolicy.RUNTIME)
public @interface Dummy {}
