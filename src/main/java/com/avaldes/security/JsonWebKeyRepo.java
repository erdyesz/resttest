package com.avaldes.security;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jose4j.jwk.JsonWebKey;
import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.jwk.RsaJwkGenerator;
import org.jose4j.lang.JoseException;

public class JsonWebKeyRepo {
	
	private static Logger logger = Logger.getLogger(JsonWebKeyRepo.class);
	
	private static List<RsaJsonWebKey> jwkList = null;
	
	private static Map<String, RsaJsonWebKey> jwkMap = null;
	
	static {    
		logger.info("Inside static initializer...");
		jwkList = new LinkedList<>(); 
		jwkMap = new HashMap<>(); 
		for (int kid = 1; kid <= 3; kid++) { 
			try {
				String keyId = String.valueOf(kid);
				RsaJsonWebKey jwk = RsaJwkGenerator.generateJwk(2048); 
				jwk.setKeyId(keyId);  
				jwkList.add(jwk); 
				jwkMap.put(keyId, jwk);
				logger.info("PUBLIC KEY (" + kid + "): " + jwk.toJson(JsonWebKey.OutputControlLevel.PUBLIC_ONLY));
			} catch (JoseException e) {
				e.printStackTrace();
			} 
		} 
	}
	
	public static RsaJsonWebKey getAt(int index) {
		return jwkList.get(index);
	}

	public static RsaJsonWebKey findByKeyId(String keyId) {
		return jwkMap.get(keyId);
	}
}
