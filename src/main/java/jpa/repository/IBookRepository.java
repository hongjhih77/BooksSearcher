package jpa.repository;

import jpa.domain.Book;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;


public interface IBookRepository {
    List<Book> findAll(BookSearchCriteria searchCriteria, Pageable pageable);

    boolean addBook(Book book);

    boolean updateBook(Book book);

    boolean deleteBook(Book book);

    Optional<Book> findBookByISBN(String isbn);
}
