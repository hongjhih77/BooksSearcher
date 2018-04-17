package io.swagger.api.gen.api;

import io.swagger.annotations.ApiParam;
import io.swagger.api.factories.BooksApiServiceFactory;
import io.swagger.api.gen.model.Books;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletConfigAware;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

@Component
@Path("/books")
@io.swagger.annotations.Api(description = "the books API")
@javax.annotation.Generated(
  value = "io.swagger.codegen.languages.java.JavaJerseyServerCodegen",
  date = "2018-04-17T14:21:51.738+08:00[Asia/Taipei]"
)
public class BooksApi{
  private final BooksApiService delegate;

  public BooksApi() {
    BooksApiService delegate = BooksApiServiceFactory.getBooksApi();
    this.delegate = delegate;
  }

  @GET
  @Path("/{isbn}")
  @Produces({"application/json"})
  @io.swagger.annotations.ApiOperation(
    value = "Info for books",
    notes = "",
    response = Books.class,
    tags = {
      "book",
    }
  )
  @io.swagger.annotations.ApiResponses(
    value = {
      @io.swagger.annotations.ApiResponse(
        code = 200,
        message = "Expected response to a valid request",
        response = Books.class
      ),
      @io.swagger.annotations.ApiResponse(
        code = 200,
        message = "unexpected error",
        response = Error.class
      )
    }
  )
  public Response booksIsbnGet(
      @ApiParam(value = "isbn10 or isbn13", required = true) @PathParam("isbn") String isbn,
      @Context SecurityContext securityContext)
      throws NotFoundException {
    return delegate.booksIsbnGet(isbn, securityContext);
  }

}
