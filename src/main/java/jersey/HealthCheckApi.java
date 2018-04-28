package jersey;

import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

@Component
@Path("/healthcheck")
public class HealthCheckApi {
  @GET
  public Response booksIsbnGet(@Context SecurityContext securityContext) {
    return Response.ok().build();
  }
}
