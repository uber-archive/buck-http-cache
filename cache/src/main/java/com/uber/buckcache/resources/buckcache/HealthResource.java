package com.uber.buckcache.resources.buckcache;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/health")
@Produces(MediaType.TEXT_PLAIN)
public class HealthResource {

  @GET
  public String getHealth() {
    return "OK";
  }
}