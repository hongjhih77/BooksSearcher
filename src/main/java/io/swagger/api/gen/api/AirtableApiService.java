package io.swagger.api.gen.api;

import io.swagger.api.gen.model.BookColumnName;

import javax.validation.constraints.NotNull;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

@javax.annotation.Generated(
  value = "io.swagger.codegen.languages.java.JavaJerseyServerCodegen",
  date = "2018-05-12T14:12:17.691+08:00[Asia/Taipei]"
)
public abstract class AirtableApiService {

  public abstract Response airtableBooksIsbnPost(BookColumnName bookcolumnname, String isbn, String airtableApiKey, @NotNull String apiUrl, SecurityContext securityContext) ;

}
