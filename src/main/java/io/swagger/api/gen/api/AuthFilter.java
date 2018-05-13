package io.swagger.api.gen.api;

import org.springframework.util.StringUtils;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;

@Priority(Priorities.AUTHENTICATION)
public class AuthFilter implements ContainerRequestFilter {

  private static String X_API_KEY_ENV = System.getenv().getOrDefault("X-API-KEY", "");

  @Override
  public void filter(ContainerRequestContext reqContext){
    String key = reqContext.getHeaderString("X-API-KEY");

    if (reqContext.getUriInfo().getPath().equals("actuator/health")) return;

    if (StringUtils.isEmpty(key) || !key.equals(X_API_KEY_ENV))
      reqContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
  }
}
