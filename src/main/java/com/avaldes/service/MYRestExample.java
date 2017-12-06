package com.avaldes.service;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


/**
 * # SAFE - UNSAFE #
 * Each HTTP method is said to be “safe” or “unsafe”. An HTTP method is “safe” if using it doesn’t modify anything on the server.
 * GET and HEAD are “safe” methods. Making a request with unsafe methods - like POST, PUT and DELETE - does change data. 
 * Actually, if you make a request with an unsafe method it may not change anything. For example, if I try to update a programmer’s avatarNumber to the value 
 * it already has, nothing happens. 
 * 
 * The point is that if a client uses an unsafe method, it knows that this method may have consequences. But if it uses a safe method, 
 * that request won’t ever have consequences. You could of course write an API that violates this. 
 * But that’s dishonest - like showing a picture of ice cream and then giving people broccoli. I like brocolli, but don’t be a jerk.
 * Being “safe” affects caching. Safe requests can be cached by a client, but unsafe requests can’t be. But caching is a whole different topic!
 * 
 * # IDEMPOTENCY #
 * Within the unsafe methods, we have to talk about the famous term: “idempotency”. A request is idempotent if the side effects of making the request 1 time is 
 * the same as making the request 2, 3, 4, or 1072 times. PUT and DELETE are idempotent, POST is not.
 * Now you can see why it seems right to say that POST creates resources and PUT updates them.
 * 
 * Deciding between POST and PUT is easy: use PUT if and only if the endpoint will follow these 2 rules:
 * 	1- The endpoint must be idempotent: so safe to redo the request over and over again;
 *  2- The URI must be the address to the resource being updated.
 *  
 *  When we use PUT, we’re saying that we want the resource that we’re sending in our request to be stored at the given URI. 
 *  We’re literally “putting” the resource at this address.
 *  
 *  This is what we’re doing when we PUT to /api/programmers/CowboyCoder. This results in an update because CowboyCoder already exists. 
 *  But imagine if we changed our controller code so that if CowboyCoder didn’t exist, it would be created. 
 *  Yes, that should still be a PUT: we’re putting the resource at this URI. Because of this, PUT is usually thought of as an update, 
 *  but it could be used to create resources. You may never choose to use PUT this way, but technically it’s ok.
 *  
 *  Heck, we could even support making a PUT request to /api/programmers. But if we did - and we followed the rules of PUT - 
 *  we’d expect the client to pass us a collection of programmers, and we’d replace the existing collection with this new one.
 *  
 *  One last thing. POST is not idempotent, so making a POST request more than one time may have additional side effects, like creating a second, 
 *  third and fourth programmer. But the key word here is may. Just because an endpoint uses POST doesn’t mean that it must have side effects on every request.
 *  It just might have side effects.
 *  
 *  When choosing between PUT and POST, don’t just say “this request is idempotent, it must be PUT!”. Instead, look at the above 2 rules for put.
 *  If it fails one of those, use POST: even if the endpoint is idempotent.
 *  
 *  # DELETE #
 *  After deleting a resource, what should the endpoint return and what about the status code? People argue about this, 
 *  but one common approach is to return a 204 status code, which means “No Content”. It’s the server’s way of saying “I completed your request ok, 
 *  but I really don’t have anything else to tell you beyond that”. In other words, the response will have an empty body.
 *  
 *  # PATCH #
 *  To get technical, PUT says “take this representation and entirely put it at this URI”. 
 *  It means that a REST API should require the client to send all of the data for a resource when updating it. 
 *  If the client doesn’t send a field, a REST API is supposed to set that field to null. So PUT is really more of a replace than an update.
 *  
 *  We’re going to follow the rules and keep our PUT implementation as a replace. But how could we allow the client to update something without sending every field?
 *  
 *  With PATCH of course! The main HTTP methods like GET, POST, DELETE and PUT were introduced in the famous RFC 2616 document. 
 *  Maybe you’ve heard of it? But because PUT has this limitation, PATCH was born in RFC 5789.
 *  
 *  Typically, PATCH is used exactly like PUT, except if we don’t send a tagLine field then it keeps its current value instead of obliterating it to null. 
 *  PATCH, it’s the friendly update.
 *  
 *  In a PUT request, the enclosed entity is considered to be a modified version of the resource stored on the origin server, 
 *  and the client is requesting that the stored version be replaced. With PATCH, however, the enclosed entity contains a set of instructions describing 
 *  how a resource currently residing on the origin server should be modified to produce a new version.
 *  
 *  Also, most of the time you want to update one or two values in a resource, not everything, so the PUT method is probably not the right solution 
 *  for partial update, which is the term used to describe such a use case. Another solution is to expose the resource’s properties you want to make editable, 
 *  and use the PUT method to send an updated value. In the example below, the email property of user 123 is exposed:
 *  	PUT /users/123/email new.email@example.org
 *  
 *  While it makes things clear, and it looks like a nice way to decide what to expose and what not to expose, this solution introduces 
 *  a lot of complexity into your API (more actions in the controllers, routing definition, documentation, etc.). 
 *  However, it is REST compliant, and a not-so-bad solution, but there is a better alternative: PATCH.
 *  
 *  PATCH is an HTTP method (a.k.a. verb) which has been described in RFC 5789. The initial idea was to propose a new way to modify existing HTTP resources. 
 *  The biggest issue with this method is that people misunderstand its usage. No! PATCH is not **strictly about sending an updated value, 
 *  rather than the entire resource** as described in the first paragraph of this article. Please, stop doing this right now! This is not correct:
 *  	PATCH /users/123 { "email": "new.email@example.org" }
 *  And, this is not correct either:
 *  	PATCH /users/123?email=new.email@example.org
 *  The PATCH method requests that a set of changes, described in the request entity, must be applied to the resource identified by the request’s URI. 
 *  This set contains instructions describing how a resource currently residing on the origin server should be modified to produce a new version. 
 *  You can think of this as a diff:
 *  	PATCH /users/123 [description of changes]
 *  
 *  The entire set of changes must be applied atomically, and the API must never provide a partially modified representation by the way. 
 *  It is worth mentioning that the request entity to PATCH is of a different content-type than the resource that is being modified. 
 *  You have to use a media type that defines semantics for PATCH, otherwise you lose the advantage of this method, and you can use either PUT or POST.
 *  From the RFC:
 *  
 *  The difference between the PUT and PATCH requests is reflected in the way the server processes the enclosed entity to modify the resource identified 
 *  by the Request-URI. In a PUT request, the enclosed entity is considered to be a modified version of the resource stored on the origin server, 
 *  and the client is requesting that the stored version be replaced. With PATCH, however, the enclosed entity contains a set of instructions describing 
 *  how a resource currently residing on the origin server should be modified to produce a new version. The PATCH method affects the resource identified 
 *  by the Request-URI, and it also MAY have side effects on other resources; i.e., new resources may be created, 
 *  or existing ones modified, by the application of a PATCH.
 *  
 *  You can use whatever format you want as [description of changes], as far as its semantics is well-defined. 
 *  That is why using PATCH to send updated values only is not suitable. RFC 6902 defines a JSON document structure for expressing 
 *  a sequence of operations to apply to a JSON document, suitable for use with the PATCH method. Here is how it looks like:
 *  
 *  [
 *   { "op": "test", "path": "/a/b/c", "value": "foo" },
 *   { "op": "remove", "path": "/a/b/c" },
 *   { "op": "add", "path": "/a/b/c", "value": [ "foo", "bar" ] },
 *   { "op": "replace", "path": "/a/b/c", "value": 42 },
 *   { "op": "move", "from": "/a/b/c", "path": "/a/b/d" },
 *   { "op": "copy", "from": "/a/b/d", "path": "/a/b/e" }
 *  ]
 *  
 *  Modifying the email of the user 123 by applying the PATCH method to its JSON representation looks like this:
 *  
 *  PATCH /users/123
 *  [
 *  	{ "op": "replace", "path": "/email", "value": "new.email@example.org" }
 *  ]
 * 
 * To sum up, the PATCH method is not a replacement for the POST or PUT methods. It applies a delta (diff) rather than replacing the entire resource. 
 * The request entity to PATCH is of a different content-type than the resource that is being modified. Instead of being an entire resource representation, 
 * it is a resource that describes changes to apply on a resource.
 * 
 * 
 * # ERROR #
 * We can copy the working scenario for creating a programmer, but remove the nickname field from the request payload. Obviously, the status code won’t be 201. 
 * Because this is a client error, we’ll need a 400-level status code. But which one? Ah, this is another spot of wonderful debate! The most common is probably 400,
 * which means simply “Bad Request”. If we look back at the RFC 2616 document, the description of the 400 status code seems to fit our situation:
 * 		The request could not be understood by the server due to malformed syntax. The client SHOULD NOT repeat the request without modifications.
 * 
 * The other common choice is 422: Unprocessable Entity. 422 comes from a different RFC and is used when the format of the data is ok, but semantically, 
 * it has errors. We’re adding validation for the business rule that a nickname is required, which is totally a semantic detail. 
 * So even though 422 seems to be less common than 400 for validation errors, it may be a more proper choice:
 * 
 * 
 * 
 * @author erdoganyesil
 *
 */
@Path("/test")
public class MYRestExample {

	@Path("/users/{username}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response helloUser(
			@PathParam("username") String userName,
			@QueryParam("salute") String salute) {
		// curl -v -X GET  http://127.0.0.1:8080/RS/test/users/zeynep?salute=selam
		if (salute == null || salute.length() == 0) {
			return Response.status(422).entity("reason=salute yok").build();
		}
		return Response.status(200).entity("OK").build();
	}
}
