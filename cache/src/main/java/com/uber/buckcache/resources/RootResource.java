package com.uber.buckcache.resources;

import java.net.URI;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

/**
 * Redirect to cache summary.
 */
@Path("/")
public class RootResource {

    @Context
    UriInfo uriInfo;

    @GET
    @Path("")
    @Produces(MediaType.TEXT_PLAIN)
    public Response index() {
        UriBuilder ub = uriInfo.getAbsolutePathBuilder();
        URI redirectURI = ub
                .path("/artifacts/summary")
                .build();

        return Response.seeOther(redirectURI).build();
    }
}
