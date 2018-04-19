package io.swagger.api.gen.api;

import io.swagger.annotations.ApiParam;
import io.swagger.api.gen.model.BooksSearchBody;
import io.swagger.api.impl.BooksApiServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
public class BooksApi {

  @Autowired private BooksApiServiceImpl delegate;

  public BooksApi() {}

  @GET
  @Path("/{isbn}")
  @Produces({"application/json"})
  @io.swagger.annotations.ApiOperation(
    value = "Info for books",
    notes = "",
    response = BooksSearchBody.class,
    tags = {
      "book",
    }
  )
  @io.swagger.annotations.ApiResponses(
    value = {
      @io.swagger.annotations.ApiResponse(
        code = 200,
        message = "Expected response to a valid request",
        response = BooksSearchBody.class
      ),
      @io.swagger.annotations.ApiResponse(
        code = 400,
        message = "Bad request. isbn must be composed of delimiter comma.",
        response = Void.class
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
