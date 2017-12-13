package com.rest.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status; 

import org.apache.log4j.Logger;
import org.jose4j.jwk.JsonWebKey;
import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.lang.JoseException;

import redis.clients.jedis.Jedis;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.rest.model.Item;
import com.rest.model.StatusMessage;
import com.rest.model.User;
import com.rest.model.builder.StatusMessageBuilder;
import com.rest.security.JsonWebKeyRepo;
import com.rest.util.JWTUtil;

 
/**
 * PUT implies putting a resource - completely replacing whatever is available at the given URL with a different thing. 
 * By definition, a PUT is idempotent. Do it as many times as you like, and the result is the same. x=5 is idempotent. 
 * You can PUT a resource whether it previously exists, or not (eg, to Create, or to Update)!
 * POST updates a resource, adds a subsidiary resource, or causes a change. A POST is not idempotent, in the way that x++ is not idempotent.
 * 
 * POST yöntemi, kaynak sunucunun istekte bulunan varlığı Request-Line'daki Request-URI tarafından tanımlanan 
 * kaynağın yeni bir alt öğesi olarak kabul etmesini istemek için kullanılır.
 * 
 * PUT yöntemi, içerilen öğenin sağlanan İstek-URI altında depolanmasını ister. Eğer İstek-URI zaten varolan bir kaynağa işaret ediyorsa, 
 * içerilen varlık, kaynak sunucuda ikamet eden birinin değiştirilmiş hali olarak düşünülmelidir * ÖNER *. 
 * İstek-URI varolan bir kaynağa işaret etmiyorsa ve bu URI, istekte bulunan kullanıcı aracısı tarafından yeni bir kaynak olarak tanımlanabiliyorsa,
 * kaynak sunucu bu URI ile kaynak oluşturabilir. "
 * 
 * POST means "create new" as in "Here is the input for creating a user, create it for me".
 * PUT means "insert, replace if already exists" as in "Here is the data for user 5".
 * You POST to example.com/users since you don't know the URL of the user yet, you want the server to create it.
 * You PUT to example.com/users/id since you want to replace/create a specific user.
 * 
 * POSTing twice with the same data means create two identical users with different ids. 
 * PUTing twice with the same data creates the user the first and updates him to the same state the second time (no changes). 
 * Since you end up with the same state after a PUT no matter how many times you perform it, it is said to be "equally potent" 
 * every time - idempotent. This is useful for automatically retrying requests. No more 'are you sure you want to resend' 
 * when you push the back button on the browser.
 * 
 * A general advice is to use POST when you need the server to be in control of URL generation of your resources. 
 * Use PUT otherwise. Prefer PUT over POST.
 * 
 * This is what AtomPub has to say about resource creation:
 * To add members to a Collection, clients send POST requests to the URI of the Collection.
 * 
 * 
 * 
 * @author erdoganyesil
 * 
 * // status
 * // curl -X GET  http://127.0.0.1:8080/RS/security/status
 * // curl -X POST -H "Content-Type: text/plain" --data "this is raw data"  http://127.0.0.1:8080/RS/security/statusp
 * 
 * // authantication
 * // curl -H 'Content-Type: application/json' -H 'username: apacheuser' -H 'password: Summer95!' -v -X GET http://127.0.0.1:8080/RS/security/authenticate
 * // curl -H 'Content-Type: application/json' -H 'username: admin005' -H 'password: Summer95!' -v -X GET http://127.0.0.1:8080/RS/security/authenticate
 * 
 * // list items 
 * //curl -H 'Content-Type: application/json' -H 'token: eyJraWQiOiIxIiwiYWxnIjoiUlMyNTYifQ.eyJpc3MiOiJhdmFsZGVzLmNvbSIsImV4cCI6MTUwOTAyMzgwMywianRpIjoiZHFoNE1KZGFpTXlDUmlnQWtCajNYdyIsImlhdCI6MTUwOTAyMzIwMywibmJmIjoxNTA5MDIzMDgzLCJzdWIiOiJhZG1pbjAwNSIsInJvbGVzIjpbIkFETUlOIiwiQ0xJRU5UIl19.jTM_8cKUf7UWfhpi3U-5tEgWz6dPk2NpLDwygecw_vZrPlWortd7LrqqLbkb6wSfTj9-qY7Rmb2at8GNqAxpHg-YwvGick_aBadEaFZJoQelvZ7tbpqgiVfvb6FEyau4jV67w56jgfISqxVGp9RvTDPlH1XAZNf0ZuxotLF2WAM9sYH5zo3tG5NNrplTGO6saatV8vjp0f_kxwyX-Oa-SFDobUiY7TJ-HKccsOvBSoWg9vu5nwJ8Hngl1y6PjdZuUJUOZNO-oMCpSSIlzacAMA2johGIbcOUWv7frZWHGWqU2hcQYtth_z09ienaD_iVT9UJb4FgFKwK3utWsZa0hA' -v -X GET  http://localhost:8080/RS/security/showallitems
 * 
 */
@Path("/security")
public class JwtSecurityRestExample {

	static Logger logger = Logger.getLogger(JwtSecurityRestExample.class);

	StatusMessageBuilder statusMessageBuilder = new StatusMessageBuilder();  
	
	Jedis jedis = new Jedis("redisdemocache.tb70wo.ng.0001.use2.cache.amazonaws.com",6379);

	@Path("/status")
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String returnVersion() {
		String message = jedis.get("status");
		if (message == null) {
			jedis.set("status", " from cache");
			return "JwtSecurityExample Status is OK at time => " + System.currentTimeMillis();
		} else {
			return "JwtSecurityExample Status is OK at time => " + System.currentTimeMillis() + message;
		}
	}
	
	@Path("/statusp")
	@POST
	@Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.TEXT_PLAIN)
	public String returnVersion2(String data) {
		return "JwtSecurityExample Status is OK..." + data;
	}
	
	@Path("/users/{username}")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String helloUser(
			@PathParam("username") String userName,
			@DefaultValue("Hello") @QueryParam("salute") String salute) {
		return salute + " " + userName;
	}

	@Path("/authenticate")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response authenticateCredentials(@HeaderParam("username") 
	String username,
	@HeaderParam("password") String password)
			throws JsonGenerationException, JsonMappingException, 
			IOException {
		logger.info("Authenticating User Credentials...");

		// #####################################################
		// input validation
		// -----------------------------------------------------
		if (username == null) {
			StatusMessage statusMessage = createStatusMessage(Status.PRECONDITION_FAILED, "Username value is missing!!!");
			return Response.status(Status.PRECONDITION_FAILED.getStatusCode())
					.entity(statusMessage).build();
		}

		if (password == null) {
			StatusMessage statusMessage = createStatusMessage(Status.PRECONDITION_FAILED, "Password value is missing!!!");

			return Response.status(Status.PRECONDITION_FAILED.getStatusCode())
					.entity(statusMessage).build();
		}

		// #####################################################
		// user validation
		// -----------------------------------------------------
		User user = validUser(username, password); 
		if (user == null) {
			StatusMessage statusMessage = createStatusMessage(Status.FORBIDDEN, "Access Denied for this functionality!!!");
			return Response.status(Status.FORBIDDEN.getStatusCode())
					.entity(statusMessage).build();
		}

		// #####################################################
		// create JWT
		// -----------------------------------------------------
		RsaJsonWebKey senderJwk = JsonWebKeyRepo.findByKeyId("1");
		senderJwk.setKeyId(senderJwk.getKeyId());
		String jwt = null;
		try {
			jwt = JWTUtil.createJWT(senderJwk, user.username, user.roles);
		} catch (JoseException e) {
			e.printStackTrace();
		}

		return Response.status(200).entity(jwt).build();
	}

	// --- Protected resource using JWT Token ---
	@Path("/showallitems")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response showAllItems(@HeaderParam("token") String token) 
			throws JsonGenerationException, JsonMappingException, IOException {

		logger.info("Inside showAllItems...");

		// #####################################################
		// input validation
		// -----------------------------------------------------
		if (token == null) {
			StatusMessage statusMessage = createStatusMessage(Status.FORBIDDEN, "Access Denied for this functionality !!!");
			return Response.status(Status.FORBIDDEN.getStatusCode())
					.entity(statusMessage).build();
		}

		// json web key
		JsonWebKey jwk = JsonWebKeyRepo.findByKeyId("1");

		// #####################################################
		// token validation
		// -----------------------------------------------------
		try {
			JwtClaims jwtClaims = JWTUtil.validateToken(jwk, token);
			logger.info("JWT validation succeeded! " + jwtClaims);
		} catch (InvalidJwtException e) {
			logger.error("JWT is Invalid: " + e);
			StatusMessage statusMessage = createStatusMessage(Status.FORBIDDEN, "JWT is invalid !!!");
			return Response.status(Status.FORBIDDEN.getStatusCode())
					.entity(statusMessage).build();
		}

		int size = 3;
		if (size == 0) {
			StatusMessage statusMessage = createStatusMessage(Status.PRECONDITION_FAILED, "There are no Items to display !!!");
			return Response.status(Status.PRECONDITION_FAILED.getStatusCode())
					.entity(statusMessage).build();
		}

		List<Item> allItems = new ArrayList<Item>();
		for (int i = 0; i < size; i++) {
			allItems.add(new Item("item"+i, i*10));
		}
		return Response.status(200).entity(allItems).build();
	}

	private User validUser(String username, String password) {
		if(username.startsWith("admin")) {
			return new User(username, Arrays.asList("ADMIN","CLIENT"));
		}
		return null;
	}

	private StatusMessage createStatusMessage(Status status, String message) {
		return statusMessageBuilder.status(status.getStatusCode())
				.message(message)
				.build();
	}
}