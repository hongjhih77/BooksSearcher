package io.swagger.api.impl;

import io.swagger.api.gen.api.ApiResponseMessage;
import io.swagger.api.gen.api.BooksApiService;
import io.swagger.api.gen.api.NotFoundException;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.java.JavaJerseyServerCodegen", date = "2018-04-17T14:21:51.738+08:00[Asia/Taipei]")

public class BooksApiServiceImpl extends BooksApiService {
    
    @Override
    public Response booksIsbnGet(String isbn, SecurityContext securityContext) throws NotFoundException {
        // do some magic!
        return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
    }
    
}

