package io.swagger.api.gen.api;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.java.JavaJerseyServerCodegen", date = "2018-04-17T14:21:51.738+08:00[Asia/Taipei]")

public abstract class BooksApiService {
    
    public abstract Response booksIsbnGet(String isbn,SecurityContext securityContext) throws NotFoundException;
    
}

