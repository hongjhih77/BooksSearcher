package io.swagger.api.impl;

import io.swagger.api.gen.api.AirtableApiService;
import io.swagger.api.gen.model.Book;
import io.swagger.api.gen.model.BookColumnName;
import jpa.repositoryimpl.BookRepositoryImpl;
import logic.updater.AirtableBooksUpdater;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotNull;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@javax.annotation.Generated(
  value = "io.swagger.codegen.languages.java.JavaJerseyServerCodegen",
  date = "2018-05-12T14:12:17.691+08:00[Asia/Taipei]"
)
@Service
public class AirtableApiServiceImpl extends AirtableApiService {

  @Autowired BookRepositoryImpl bookRepository;

  @Override
  public Response airtableBooksIsbnPost(BookColumnName bookcolumnname, String isbn, String airtableApiKey, @NotNull String apiUrl, SecurityContext securityContext) {

    // do some magic!

    String regex = "[0-9, /,]+";
    if(!isbn.matches(regex)) return Response.status(Response.Status.BAD_REQUEST).build();

    String[] _isbns = isbn.split(",");
    List<String> bookNotFoundList = new ArrayList<>();
    List<jpa.domain.Book> bookList = new ArrayList<>();

    // region find books
    bookRepository.findBooks(_isbns, bookNotFoundList, bookList);
    // endregion

    Map<String, Integer> resultStatusCodeMap = new HashMap<>();
    for( jpa.domain.Book book :bookList){
      io.restassured.response.Response response = AirtableBooksUpdater.createARecord(apiUrl, airtableApiKey, bookcolumnname, book);
      resultStatusCodeMap.put(!StringUtils.isEmpty(book.getIsbn13())? book.getIsbn13() : book.getIsbn10(), response.statusCode());
    }

    return Response.ok().entity(resultStatusCodeMap).build();
  }
}
