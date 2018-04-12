package jpa.repositoryimpl;

import jersey.SampleJerseyApplication;
import jpa.domain.Book;
import jpa.repository.BookSearchCriteria;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@RunWith(SpringRunner.class)

@SpringBootTest(classes = SampleJerseyApplication.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
public class BookRepositoryImplTest {

    @Autowired
    BookRepositoryImpl bookRepository;

    @Test
    public void findAll() {

        Book book = new Book("first book1");
        book.setIsbn10("0375725784");
        book.setIsbn13("9780375725784");
        assertThat(bookRepository.addBook(book)).isTrue();

        BookSearchCriteria criteria = new BookSearchCriteria();
        Pageable pageable = PageRequest.of(0, 10);
        List<Book> books = bookRepository.findAll(criteria, pageable);
        assertThat(books.size()).isEqualTo(1);
        assertThat(books.get(0).getIsbn13()).isEqualTo("9780375725784");
    }

    @Test
    public void addBook() {
        Book book = new Book("first book");
        assertThat(bookRepository.addBook(book)).isTrue();
    }

    @Test
    public void updateBook() {
        Book book = new Book("first book1");
        book.setIsbn10("0375725784");
        book.setIsbn13("9780375725784");
        assertThat(bookRepository.addBook(book)).isTrue();
        book.setIsbn13("9780375725785");
        assertThat(bookRepository.updateBook(book)).isTrue();

        BookSearchCriteria criteria = new BookSearchCriteria();
        Pageable pageable = PageRequest.of(0, 10);
        List<Book> books = bookRepository.findAll(criteria, pageable);
        assertThat(books.size()).isEqualTo(1);
        assertThat(books.get(0).getIsbn13()).isEqualTo("9780375725785");
    }

    @Test
    public void deleteBook() {
        Book book = new Book("first book1");
        book.setIsbn10("0375725784");
        book.setIsbn13("9780375725784");
        assertThat(bookRepository.addBook(book)).isTrue();
        assertThat(bookRepository.deleteBook(book)).isTrue();

        BookSearchCriteria criteria = new BookSearchCriteria();
        Pageable pageable = PageRequest.of(0, 10);
        List<Book> books = bookRepository.findAll(criteria, pageable);
        assertThat(books.size()).isEqualTo(0);
    }
}