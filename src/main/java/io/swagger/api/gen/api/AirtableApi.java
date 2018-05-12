package io.swagger.api.gen.api;

import io.swagger.annotations.ApiParam;
import io.swagger.api.gen.model.Book;
import io.swagger.api.gen.model.BookColumnName;
import io.swagger.api.impl.AirtableApiServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.ServletConfig;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

@Component
@Path("/airtable")
@io.swagger.annotations.Api(description = "the airtable API")
@javax.annotation.Generated(
  value = "io.swagger.codegen.languages.java.JavaJerseyServerCodegen",
  date = "2018-05-12T14:12:17.691+08:00[Asia/Taipei]"
)
public class AirtableApi {

  @Autowired private AirtableApiServiceImpl delegate;

  public AirtableApi() {}

  @POST
  @Path("/books/{isbn}")
  @Consumes({ "application/json" })
  @Produces({ "application/json" })
  @io.swagger.annotations.ApiOperation(value = "save boos to airtable", notes = "", response = Object.class, tags={ "book", })
  @io.swagger.annotations.ApiResponses(value = {
          @io.swagger.annotations.ApiResponse(code = 200, message = "Save books to Airtable successfully", response = Object.class),

          @io.swagger.annotations.ApiResponse(code = 400, message = "Bad request.", response = Error.class),

          @io.swagger.annotations.ApiResponse(code = 401, message = "API key is missing or invalid", response = Void.class) })
  public Response airtableBooksIsbnPost(@ApiParam(value = "name of columns needed to save the books to corresponding slot" ,required=true) BookColumnName bookcolumnname
          ,@ApiParam(value = "isbn10 or isbn13",required=true) @PathParam("isbn") String isbn
          ,@ApiParam(value = "api key from provided by AirTable" ,required=true)@HeaderParam("airtableApiKey") String airtableApiKey
          ,@ApiParam(value = "the entire url provided by AirTable",required=true) @QueryParam("apiUrl") String apiUrl
          ,@Context SecurityContext securityContext)
          throws NotFoundException {
    return delegate.airtableBooksIsbnPost(bookcolumnname,isbn,airtableApiKey,apiUrl,securityContext);
  }

}
