package logic.updater;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;
import io.swagger.api.gen.model.BookColumnName;
import jpa.domain.Book;
import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class AirtableBooksUpdaterTest {


  public void testEndpoint() {
    String expected = "https://api.airtable.com/v0/appVe5KDKrdOCIKX3/Books";
    try {
      String result = AirtableBooksUpdater.endpoint("v0", "appVe5KDKrdOCIKX3", "Books");
      assertThat(result).isEqualTo(expected);
    } catch (URISyntaxException e) {
      e.printStackTrace();
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }

  }


  public void testCreateARecord() throws ParseException {
    BookColumnName bookWithColumnNames = new BookColumnName();
    bookWithColumnNames.setIsbn13("isbn13");
    bookWithColumnNames.setIsbn10("isbn10");
    bookWithColumnNames.setTitle("title");
    bookWithColumnNames.setAuthor("author");
    bookWithColumnNames.setOriginalTile("originalTile");
    bookWithColumnNames.setPages("pages");
    bookWithColumnNames.setPublishDate("publishDate");
    bookWithColumnNames.setPublisher("publisher");

    Book book = new Book("斷裂2097");
    book.setIsbn13("9789869435123");
    book.setOriginalTile("Time Salvager");
    book.setAuthor("Wesley Chu");
    book.setPages(464);
    book.setPublisher("鸚鵡螺文化");
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    book.setPublishTime(format.parse("2018-05-09").getTime());

    try {
      String endpoint = AirtableBooksUpdater.endpoint("v0", "appVe5KDKrdOCIKX3", "BooksTest");
      Response result = AirtableBooksUpdater.createARecord(endpoint,"keykXkqTcmMTZGAik", bookWithColumnNames, book);
      assertThat(result.getStatusCode()).isEqualTo(HttpStatus.SC_OK);
    } catch (MalformedURLException e) {
      e.printStackTrace();
    } catch (URISyntaxException e) {
      e.printStackTrace();
    }

  }

  public void testGetRequestBody() throws ParseException {

    String expectedString =
        "{\"fields\": {\"title\": \"斷裂2097\",\"isbn13\": \"9789869435123\",\"originalTile\": \"Time Salvager\",\"pages\": \"464\",\"author\": \"Wesley Chu\",\"publishDate\": \"2018-05-09\",\"publisher\": \"鸚鵡螺文化\"}}";

    ObjectMapper mapper = new ObjectMapper();
    TypeReference<HashMap<String,Object>> typeRef
            = new TypeReference<HashMap<String,Object>>() {};

    BookColumnName bookWithColumnNames = new BookColumnName();
    bookWithColumnNames.setIsbn13("isbn13");
    bookWithColumnNames.setIsbn10("isbn10");
    bookWithColumnNames.setTitle("title");
    bookWithColumnNames.setAuthor("author");
    bookWithColumnNames.setOriginalTile("originalTile");
    bookWithColumnNames.setPages("pages");
    bookWithColumnNames.setPublishDate("publishDate");
    bookWithColumnNames.setPublisher("publisher");

    Book book = new Book("斷裂2097");
    book.setIsbn13("9789869435123");
    book.setOriginalTile("Time Salvager");
    book.setAuthor("Wesley Chu");
    book.setPages(464);
    book.setPublisher("鸚鵡螺文化");
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    book.setPublishTime(format.parse("2018-05-09").getTime());

    String resultJson = AirtableBooksUpdater.getRequestBody(bookWithColumnNames,book).get();

    try {
      HashMap<String,Object> mapExpected = mapper.readValue(expectedString, typeRef);
      LinkedHashMap<String, String> likedHashMapExpected = (LinkedHashMap<String, String>) mapExpected.get("fields");
      HashMap<String,Object> mapResult = mapper.readValue(resultJson, typeRef);
      LinkedHashMap<String, String> likedHashMapResult = (LinkedHashMap<String, String>) mapResult.get("fields");

      LinkedHashMap<String, String>  likedHashMapExpected_sorted = new LinkedHashMap<>();
      likedHashMapExpected.entrySet().stream()
              .sorted(Map.Entry.comparingByKey()).forEach( entry -> likedHashMapExpected_sorted.put(entry.getKey(), entry.getValue()));

      LinkedHashMap<String, String>  likedHashMapResult_sorted = new LinkedHashMap<>();
      likedHashMapResult.entrySet().stream()
              .sorted(Map.Entry.comparingByKey()).forEach(entry -> likedHashMapResult_sorted.put(entry.getKey(), entry.getValue()));

      assertThat(likedHashMapExpected_sorted.toString()).isEqualTo(likedHashMapResult_sorted.toString());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}