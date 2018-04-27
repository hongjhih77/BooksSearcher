package jpa.repositoryimpl;

import jersey.SampleJerseyApplication;
import jpa.domain.Book;
import jpa.repository.BookSearchCriteria;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SampleJerseyApplication.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
public class BookRepositoryImplTest {

  @Autowired BookRepositoryImpl bookRepository;

  Book _book = null;

  @Before
  public void prepareBook() {
    _book = new Book("Test Book");
    _book.setIsbn10("1111111111");
    _book.setIsbn13("1111111111111");
  }

  @Test
  public void findAll() {
    assertThat(bookRepository.addBook(_book)).isTrue();
    BookSearchCriteria criteria = new BookSearchCriteria();
    Pageable pageable = PageRequest.of(0, 10);
    List<Book> books = bookRepository.findAll(criteria, pageable);
    assertThat(books.size() > 0).isTrue();
    assertThat(books.size() <= 10).isTrue();
  }

  @Ignore
  public void addBook() {
    // findAll()
  }

  @Test
  public void updateBook() {
    assertThat(bookRepository.addBook(_book)).isTrue();
    _book.setTitle("Updated Book Title");
    assertThat(bookRepository.updateBook(_book)).isTrue();
  }

  @Test
  public void deleteBook() {
    assertThat(bookRepository.addBook(_book)).isTrue();
    assertThat(bookRepository.deleteBook(_book)).isTrue();
  }
}
