package logic.parser.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jpa.domain.Book;
import logic.parser.Hendler.BookParserHandler;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Locale;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class AmazonBookParserTest {

  @DataProvider(name = "testBook")
  public Object[][] createData() {
    return new Object[][] {
      {
        "9780375725784",
        "A Heartbreaking Work of Staggering Genius",
        "9780375725784",
        "0375725784",
        437,
        "Dave Eggers",
        "Vintage",
        "February 13, 2001",
        "https://www.amazon.com/Heartbreaking-Work-Staggering-Genius/dp/0375725784"
      }
    };
  }

  @Test(dataProvider = "testBook", groups = "BookParser.Amazon")
  public void getBook(
      String isbn,
      String title,
      String ISBN13,
      String ISBN10,
      int pages,
      String authors,
      String publisher,
      String publishedDate,
      String itemUrl)
      throws ParseException {
    BookParserHandler iBookParser = new AmazonBookParser();
    Optional<Book> bookParsed = iBookParser.getBook(isbn);

    Book book_correct = new Book(title);
    book_correct.setIsbn13(ISBN13);
    book_correct.setIsbn10(ISBN10);
    book_correct.setPages(pages);
    book_correct.setAuthor(authors);
    book_correct.setPublisher(publisher);
    book_correct.setItemUrl(itemUrl);

    SimpleDateFormat format = new SimpleDateFormat("MMMM dd, yyyy", Locale.US);
    Instant instant = format.parse(publishedDate).toInstant();
    long epochMillis = instant.toEpochMilli();
    book_correct.setPublishTime(epochMillis);

    ObjectMapper mapper = new ObjectMapper();
    try {
      String correctJson = mapper.writeValueAsString(book_correct);
      String bookParsedJson = mapper.writeValueAsString(bookParsed.get());
      assertThat(correctJson).isEqualTo(bookParsedJson);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
  }
}
