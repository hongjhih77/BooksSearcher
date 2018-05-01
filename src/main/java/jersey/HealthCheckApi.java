package jersey;

import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.logging.Logger;

@Component
@Path("/healthcheck")
public class HealthCheckApi {
  private static final String PLATFORM =
          System.getenv().getOrDefault("PLATFORM", Logger.GLOBAL_LOGGER_NAME);
  @GET
  public Response booksIsbnGet(@Context SecurityContext securityContext) {
    return Response.ok("PLATFORM = " + PLATFORM).build();
  }
}
