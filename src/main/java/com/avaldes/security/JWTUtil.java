package com.avaldes.security;

import java.util.List;

import org.jose4j.jwk.JsonWebKey;
import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.lang.JoseException;

public class JWTUtil {
	
	public static String createJWT(RsaJsonWebKey senderJwk, String userName, List<String> userRoles) throws JoseException {
	    JsonWebSignature jws = new JsonWebSignature();
	    JwtClaims claims = createClaimsForUser(userName, userRoles);

	    jws.setPayload(claims.toJson());
	    jws.setKeyIdHeaderValue(senderJwk.getKeyId());
	    jws.setKey(senderJwk.getPrivateKey());
	    jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256); 

	    return jws.getCompactSerialization();
	}
	
	public static JwtClaims validateToken(JsonWebKey jwk, String token) throws InvalidJwtException {
	    // Validate Token's authenticity and check claims
	    JwtConsumer jwtConsumer = new JwtConsumerBuilder()
	      .setRequireExpirationTime()
	      .setAllowedClockSkewInSeconds(30)
	      .setRequireSubject()
	      .setExpectedIssuer("avaldes.com")
	      .setVerificationKey(jwk.getKey())
	      .build();

	    return jwtConsumer.processToClaims(token);
	}
	
	private static JwtClaims createClaimsForUser(String userName, List<String> userRoles) {
	    JwtClaims claims = new JwtClaims();
	    // The iss (issuer) claim identifies the principal that issued the JWT. The processing of this claim is 
	    // generally application specific. The iss value is a case-sensitive string containing a StringOrURI value. 
	    // Use of this claim is OPTIONAL.
	    claims.setIssuer("avaldes.com");
	    
	    // The exp (expiration time) claim identifies the expiration time on or after which the JWT MUST NOT be accepted for processing.
	    // The processing of the exp claim requires that the current date/time MUST be before the expiration date/time listed 
	    // in the exp claim. Implementers MAY provide for some small leeway, usually no more than a few minutes, to account for clock skew. 
	    // Its value MUST be a number containing a NumericDate value. Use of this claim is OPTIONAL.
	    claims.setExpirationTimeMinutesInTheFuture(10);
	    
	    // The jti (JWT ID) claim provides a unique identifier for the JWT. The identifier value MUST be assigned in a manner 
	    // that ensures that there is a negligible probability that the same value will be accidentally assigned to a different data object;
	    // if the application uses multiple issuers, collisions MUST be prevented among values produced by different issuers as well. 
	    // The jti claim can be used to prevent the JWT from being replayed. The jti value is a case-sensitive string. 
	    // Use of this claim is OPTIONAL.
	    claims.setGeneratedJwtId();
	    
	    // The iat (issued at) claim identifies the time at which the JWT was issued. This claim can be used to determine 
	    // the age of the JWT. Its value MUST be a number containing a NumericDate value. Use of this claim is OPTIONAL.
	    claims.setIssuedAtToNow();
	    
	    // The nbf (not before) claim identifies the time before which the JWT MUST NOT be accepted for processing.
	    // The processing of the nbf claim requires that the current date/time MUST be after or equal to the not-before date/time
	    // listed in the nbf claim. Implementers MAY provide for some small leeway, usually no more than a few minutes, 
	    // to account for clock skew. Its value MUST be a number containing a NumericDate value. Use of this claim is OPTIONAL.
	    claims.setNotBeforeMinutesInThePast(2);
	    
	    // The sub (subject) claim identifies the principal that is the subject of the JWT. The claims in a JWT are normally statements 
	    // about the subject. The subject value MUST either be scoped to be locally unique in the context of the issuer or 
	    // be globally unique. The processing of this claim is generally application specific. The sub value is a case-sensitive string 
	    // containing a StringOrURI value. Use of this claim is OPTIONAL.
	    claims.setSubject(userName);
	    
	    claims.setStringListClaim("roles", userRoles); 
	    return claims;
	}

}
