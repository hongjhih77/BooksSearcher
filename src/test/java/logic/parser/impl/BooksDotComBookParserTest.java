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

public class BooksDotComBookParserTest {

  @DataProvider(name = "testBook")
  public Object[][] createData1() {
    return new Object[][] {
      {
        "9789862419830",
        "Google模式：挑戰瘋狂變化世界的經營思維與工作邏輯",
        "How Google Works",
        "9789862419830",
        352,
        "Eric Schmidt, Jonathan Rosenberg",
        "天下雜誌",
        "2014/11/07",
        "http://www.books.com.tw/products/0010656961"
      },
      {
        "9789579609203",
        "第十年的情人節",
        "素敵な日本人",
        "9789579609203",
        288,
        "東野圭吾",
        "春天出版社",
        "2018/02/28",
        "http://www.books.com.tw/products/0010780322"
      },
      {
        "9789864002047",
        "在家自學的四個孩子",
        null,
        "9789864002047",
        224,
        "許惠珺",
        "道聲",
        "2018/04/10",
        "http://www.books.com.tw/products/0010783929"
      }
    };
  }

  @Test(dataProvider = "testBook")
  public void testGetBook(
      String key,
      String title,
      String originalTile,
      String ISBN13,
      int pages,
      String authors,
      String publisher,
      String publishedDate,
      String itemUrl)
      throws ParseException {

    BookParserHandler iBookParser = new BooksDotComBookParser();
    Optional<Book> bookParsed = iBookParser.getBook(key);

    Book book_correct = new Book(title);
    book_correct.setOriginalTile(originalTile);
    book_correct.setIsbn13(ISBN13);
    book_correct.setPages(pages);
    book_correct.setAuthor(authors);
    book_correct.setPublisher(publisher);
    book_correct.setItemUrl(itemUrl);

    SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd", Locale.TAIWAN);
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
