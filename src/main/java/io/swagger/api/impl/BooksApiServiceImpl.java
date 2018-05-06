package io.swagger.api.impl;

import io.swagger.api.gen.api.BooksApiService;
import io.swagger.api.gen.model.BooksSearchBody;
import jpa.domain.Book;
import jpa.repositoryimpl.BookRepositoryImpl;
import logic.parser.Hendler.BookParserHandler;
import logic.parser.impl.AmazonBookParser;
import logic.parser.impl.BooksDotComBookParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@javax.annotation.Generated(
  value = "io.swagger.codegen.languages.java.JavaJerseyServerCodegen",
  date = "2018-04-17T14:21:51.738+08:00[Asia/Taipei]"
)
@Service
public class BooksApiServiceImpl extends BooksApiService {

  @Autowired BookRepositoryImpl bookRepository;

  @Override
  public Response booksIsbnGet(String isbns, SecurityContext securityContext) {

    String regex = "[0-9, /,]+";
    if(!isbns.matches(regex)) return Response.status(Response.Status.BAD_REQUEST).build();

    String[] _isbns = isbns.split(",");
    List<String> bookNotFoundList = new ArrayList<>();
    List<Book> bookList = new ArrayList<>();

    // region find books
    for (String ISBN : _isbns) {
      Optional<Book> bookOptional = bookRepository.findBookByISBN(ISBN);
      if (bookOptional.isPresent()) {
        bookList.add(bookOptional.get());
        continue;
      }
      BookParserHandler amazonHandler = new AmazonBookParser();
      BookParserHandler bookDotComHandler = new BooksDotComBookParser();
      amazonHandler.setSuccessor(bookDotComHandler);
      bookOptional = amazonHandler.processRequest(ISBN);
      if (bookOptional.isPresent()) {
        bookList.add(bookOptional.get());
        bookRepository.addBook(bookOptional.get());
      } else {
        bookNotFoundList.add(ISBN);
      }
    }
    // endregion

    // region set response
    Function<Book, io.swagger.api.gen.model.Book> bookEntityToModelFunction =
        new Function<Book, io.swagger.api.gen.model.Book>() {
          @Override
          public io.swagger.api.gen.model.Book apply(Book book) {
            io.swagger.api.gen.model.Book _bookModel = new io.swagger.api.gen.model.Book();
            _bookModel.setIsbn10(book.getIsbn10());
            _bookModel.setIsbn13(book.getIsbn13());
            _bookModel.setTitle(book.getTitle());
            _bookModel.setPages(book.getPages());
            _bookModel.setPublisher(book.getPublisher());
            _bookModel.setOriginalTile(book.getOriginalTile());
            _bookModel.setAuthor(book.getAuthor());
            return _bookModel;
          }
        };

    List<io.swagger.api.gen.model.Book> books =
        bookList.stream().map(bookEntityToModelFunction).collect(Collectors.toList());

    BooksSearchBody booksSearchBody =
        new BooksSearchBody()
            .notFound(bookNotFoundList.size())
            .notFoundList(bookNotFoundList)
            .booksFoundList(books);
    // endregion

    return Response.ok().entity(booksSearchBody).build();
  }
}
