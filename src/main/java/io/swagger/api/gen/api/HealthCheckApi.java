package io.swagger.api.gen.api;

import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Component
@Path("/healthcheck")
public class HealthCheckApi {
  @GET
  @Produces({"application/json"})
  public Response booksIsbnGet() {
    return Response.ok().build();
  }
}
