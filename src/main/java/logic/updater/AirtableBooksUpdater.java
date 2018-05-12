package logic.updater;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.swagger.api.gen.model.BookColumnName;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.util.StringUtils;
import util.LogService;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

import static io.restassured.RestAssured.given;

public class AirtableBooksUpdater {

  private static final String protocol = "https";
  private static final String host = "api.airtable.com";

  public static String endpoint(String version, String appId, String tableName)
      throws URISyntaxException, MalformedURLException {
    URIBuilder builder = new URIBuilder();
    builder.setScheme(protocol);
    builder.setHost(host);
    builder.setPath("/" + version + "/" + appId + "/" + tableName);
    URL url = builder.build().toURL();
    return url.toString();
  }

  public static Response createARecord(
      String apiUrl, String apiKey, BookColumnName bookWithColumnNames, jpa.domain.Book book) {
    Optional<String> optional_requestBody = getRequestBody(bookWithColumnNames, book);
    if (!optional_requestBody.isPresent())
      throw new IllegalArgumentException("Cannot get request body. bookWithColumnNames = " + bookWithColumnNames.toString());

    RequestSpecBuilder builder = new RequestSpecBuilder();
    builder.setBody(optional_requestBody.get());
    builder.setContentType("application/json; charset=UTF-8");
    RequestSpecification requestSpec = builder.build();

    Response response =
        given()
            .spec(requestSpec)
            .header("Authorization", "Bearer " + apiKey)
            .and()
            .header("Content-type", "application/json")
            .when()
            .post(apiUrl);
    return response;
  }

  public static Optional<String> getRequestBody(
      BookColumnName bookWithColumnNames, jpa.domain.Book book) {

    ObjectMapper mapper = new ObjectMapper();

    Map<String, String> fields = new LinkedHashMap<>();

    if (!StringUtils.isEmpty(bookWithColumnNames.getTitle())
        && !StringUtils.isEmpty(book.getTitle()))
      fields.put(bookWithColumnNames.getTitle(), book.getTitle());

    if (!StringUtils.isEmpty(bookWithColumnNames.getIsbn10())
        && !StringUtils.isEmpty(book.getIsbn10()))
      fields.put(bookWithColumnNames.getIsbn10(), book.getIsbn10());

    if (!StringUtils.isEmpty(bookWithColumnNames.getIsbn13())
        && !StringUtils.isEmpty(book.getIsbn13()))
      fields.put(bookWithColumnNames.getIsbn13(), book.getIsbn13());

    if (!StringUtils.isEmpty(bookWithColumnNames.getAuthor())
        && !StringUtils.isEmpty(book.getAuthor()))
      fields.put(bookWithColumnNames.getAuthor(), book.getAuthor());

    if (!StringUtils.isEmpty(bookWithColumnNames.getOriginalTile())
        && !StringUtils.isEmpty(book.getOriginalTile()))
      fields.put(bookWithColumnNames.getOriginalTile(), book.getOriginalTile());

    if (!StringUtils.isEmpty(bookWithColumnNames.getPages()))
      fields.put(bookWithColumnNames.getPages(), String.valueOf(book.getPages()));

    if (!StringUtils.isEmpty(bookWithColumnNames.getPublisher())
        && !StringUtils.isEmpty(book.getPublisher()))
      fields.put(bookWithColumnNames.getPublisher(), book.getPublisher());

    if (!StringUtils.isEmpty(bookWithColumnNames.getPublishDate()) && book.getPublishTime() > 0) {
      Date date = new Date(book.getPublishTime());
      SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd");
      fields.put(bookWithColumnNames.getPublishDate(), fm.format(date));
    }

    Map<String, Object> returnMap = new HashMap<>();
    returnMap.put("fields", fields);

    try {
      return Optional.of(mapper.writeValueAsString(returnMap));
    } catch (JsonProcessingException e) {
      LogService.error(e, AirtableBooksUpdater.class);
    }

    return Optional.empty();
  }
}
