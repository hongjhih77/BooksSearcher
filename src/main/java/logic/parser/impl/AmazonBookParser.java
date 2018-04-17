package logic.parser.impl;

import jpa.domain.Book;
import logic.parser.Hendler.BookParserHandler;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static javax.ws.rs.core.HttpHeaders.USER_AGENT;

public class AmazonBookParser extends BookParserHandler {

  @Override
  public Optional<Book> getBook(String isbn) {

    String urlSearch = "https://www.amazon.com/gp/search/ref=sr_adv_b/?search-alias=stripbooks&unfiltered=1&field-keywords=&field-author=&field-title=&field-isbn=" + isbn + "&field-publisher=&node=&field-p_n_condition-type=&p_n_feature_browse-bin=&field-age_range=&field-language=&field-dateop=During&field-datemod=&field-dateyear=&sort=relevanceexprank&Adv-Srch-Books-Submit.x=32&Adv-Srch-Books-Submit.y=9";

    try {
      String urlBook = getItemUrl(urlSearch);

      Connection connection = getConnection(urlBook);
      Document htmlDocument = connection.get();

      //get title
      String title = getTitle(htmlDocument);

      //get Authors
      List<String> authors = getAuthors(htmlDocument);
      String authorString = String.join(", ", authors);

      Element elementProductDetailsTable = htmlDocument.getElementById("productDetailsTable");
      Elements elelis = elementProductDetailsTable.getElementsByTag("li");

      //get pages
      String columnValue_pages = getColumnValueFromDetail(elelis, "Paperback");
      String pageString = columnValue_pages.replace("pages", "").trim();
      int pages = Integer.valueOf(pageString);

      //get published Date time
      long publishedDateMillis = getPublishedDateMillis(elelis);

      //get publisher
      String columnValue_Publisher = getColumnValueFromDetail(elelis, "Publisher");
      String publisher = columnValue_Publisher.substring(0, columnValue_Publisher.indexOf(";")).trim();

      //get Lang
      String language = getColumnValueFromDetail(elelis, "Language").trim();

      // get ISBN10
      String ISBN10 = getColumnValueFromDetail(elelis, "ISBN-10").trim();

      //get ISBN13
      String ISBN13 = getColumnValueFromDetail(elelis, "ISBN-13").trim().replace("-", "");

      Book _book = new Book(title);
      _book.setPublishTime(publishedDateMillis);
      _book.setPublisher(publisher);
      _book.setPages(pages);
      _book.setIsbn10(ISBN10);
      _book.setIsbn13(ISBN13);
      _book.setAuthor(authorString);
      _book.setItemUrl(urlBook);

      return Optional.of(_book);
    } catch (Exception e) {
      e.printStackTrace();
      //TODO LOG
    }

    return Optional.empty();
  }

  private long getPublishedDateMillis(Elements elelis) {
    long epochMillis = 0;
    String columnValue_Publisher = getColumnValueFromDetail(elelis, "Publisher");

    try {
      String formattedDate = columnValue_Publisher.substring(columnValue_Publisher.indexOf("(") + 1, columnValue_Publisher.indexOf(")"));
      SimpleDateFormat format = new SimpleDateFormat("MMMM dd, yyyy", Locale.US);
      Instant instant = format.parse(formattedDate).toInstant();
      epochMillis = instant.toEpochMilli();
    } catch (ParseException e) {
      //TODO LOG
    }
    return epochMillis;
  }

  private String getColumnValueFromDetail(Elements elelis, String columnInDetail) {
    String columnValue = null;

    for (Element li : elelis) {
      Node br = li.childNodes().get(0);
      if (((TextNode) br.childNode(0)).getWholeText().contains(columnInDetail)) {
        columnValue = ((TextNode) li.childNodes().get(1)).getWholeText();
        break;
      }
    }
    return columnValue;
  }

  private List<String> getAuthors(Document htmlDocument) {
    Element bylineEle = htmlDocument.getElementById("byline");
    List<String> authors = new ArrayList<>();

    try {
      for (Element element : bylineEle.getElementsByTag("table")) {
        Element authorEle = element.getElementsByClass("a-size-medium").first();
        String author = ((TextNode) authorEle.childNodes().get(0)).getWholeText().trim();
        authors.add(author);
      }
    } catch (Exception e) {
      //TODO LOG
    }
    return authors;
  }

  private String getTitle(Document htmlDocument) {
    Element productTitleEle = htmlDocument.getElementById("productTitle");
    return ((TextNode) productTitleEle.childNodes().get(0)).getWholeText();
  }

  private String getItemUrl(String urlSearch) throws IOException, URISyntaxException {
    Connection connectionSearch = getConnection(urlSearch);
    Document htmlDocument = connectionSearch.get();
    Element element_result_0 = htmlDocument.getElementById("result_0");
    Elements element_title = element_result_0.getElementsByClass("a-link-normal s-access-detail-page  s-color-twister-title-link a-text-normal");
    String itemUrl = element_title.first().attr("href");
    URI uri = new URI(itemUrl);
    String[] strings = (uri.getHost() + uri.getPath()).split("[/]");
    String[] cleanStrings = {strings[0], strings[1], strings[2], strings[3]};
    itemUrl = String.join("/", cleanStrings);
    return "https://" + itemUrl;
  }

  private Connection getConnection(String url) {
    return Jsoup.connect(url).userAgent(USER_AGENT);
  }
}
