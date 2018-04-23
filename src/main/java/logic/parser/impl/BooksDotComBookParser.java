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
import util.Logservice;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static javax.ws.rs.core.HttpHeaders.USER_AGENT;

public class BooksDotComBookParser extends BookParserHandler {
  @Override
  public Optional<Book> getBook(String isbn) {

    String urlSearch = "http://search.books.com.tw/search/query/key/" + isbn + "/cat/all";

    String itemUrl = getItemUrl(urlSearch);

    System.out.println("itemUrl : " + itemUrl);

    try {
      Connection connection = getConnection(itemUrl);
      Document htmlDocument = connection.get();

      // get title
      Elements titleEles = htmlDocument.select("h1[itemprop=name]");
      String title = ((TextNode) titleEles.get(0).childNode(0)).getWholeText();

      // get original title
      String originalTitle = null;
      try {
        originalTitle =
            ((TextNode)
                    titleEles
                        .get(0)
                        .parent()
                        .getElementsByTag("h2")
                        .get(0)
                        .children()
                        .get(0)
                        .childNodes()
                        .get(0))
                .getWholeText()
                .trim();
      } catch (Exception e) {
        Logservice.error(e, this.getClass());
      }

      // get authors
      // itemprop="author"
      Elements authorEles = htmlDocument.select("li[itemprop=author]");
      Elements authorEleSibling = authorEles.get(0).parent().children();
      boolean hasOriginalAuthorName = authorEles.parents().toString().contains("原文作者");

      List<String> authors = new ArrayList<>();

      if (hasOriginalAuthorName) {
        for (Element sibling : authorEleSibling) {
          if (!sibling.toString().contains("原文作者")) continue;
          for (Node node : sibling.childNodes()) {
            if (node instanceof Element) {
              for (Node node_author : node.childNodes()) {
                if (node_author instanceof TextNode) {
                  String author = ((TextNode) node_author).getWholeText().trim();
                  if (author.length() > 0)
                    authors.add(((TextNode) node_author).getWholeText().trim());
                }
              }
            }
          }
        }
      } else {
        List<Node> nodes_author_group = authorEles.get(0).childNodes();
        boolean startRecordElement = false;
        for (int i = 0; i < nodes_author_group.size(); i++) {
          Node node = nodes_author_group.get(i);
          if (node instanceof TextNode && ((TextNode) node).getWholeText().contains("作者")) {
            startRecordElement = true;
          } else if (node instanceof Element && node.toString().contains("trace_btn1")) {
            startRecordElement = false;
          } else if (node instanceof Element && startRecordElement) {
            for (Node node_author : node.childNodes()) {
              if (node_author instanceof TextNode) {
                String author = ((TextNode) node_author).getWholeText().trim();
                if (author.length() > 0) authors.add(author);
              }
            }
          }
        }
      }

      // get publisher
      String publisher = null;
      // <span itemprop="brand">天下雜誌</span>
      Elements eles_publisher = htmlDocument.select("span[itemprop=brand]");
      for (Node node : eles_publisher.get(0).childNodes()) {
        if (node instanceof TextNode) {
          String _publisher = ((TextNode) node).getWholeText().trim();
          if (_publisher.length() > 0) publisher = _publisher;
        }
      }

      // get published date
      long publishedDateMilli = 0;
      for (Element sibling : authorEleSibling) {
        if (!sibling.toString().contains("出版日期")) continue;
        for (Node node : sibling.childNodes()) {
          if (node instanceof TextNode) {
            String formattedDate = ((TextNode) node).getWholeText().trim().split("：")[1].trim();
            SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd", Locale.TAIWAN);
            Instant instant = format.parse(formattedDate).toInstant();
            publishedDateMilli = instant.toEpochMilli();
          }
        }
      }

      // get Language
      String lang = null;
      for (Element sibling : authorEleSibling) {
        if (!sibling.toString().contains("語言")) continue;
        for (Node node : sibling.childNodes()) {
          if (node instanceof TextNode) {
            lang = ((TextNode) node).getWholeText().trim().split("：")[1].trim();
          }
        }
      }

      // <meta itemprop="productID" content="isbn:9789862419830">
      // get isbn13
      Elements eles_productID = htmlDocument.select("meta[itemprop=productID]");
      String isbn13 = eles_productID.get(0).attr("content").split(":")[1];

      // get pages
      // <div class="bd">
      int pages = 0;
      Elements eles_bd = htmlDocument.select("div[class=bd]");
      for (Element element_in_db : eles_bd) {
        if (element_in_db.toString().contains("規格")) {
          Elements lis = element_in_db.getElementsByTag("li");
          for (Element li : lis) {
            if (li.toString().contains("規格")) {
              for (Node node : li.childNodes()) {
                if (node instanceof TextNode) {
                  String pageString =
                      ((TextNode) node)
                          .getWholeText()
                          .trim()
                          .split("：")[1]
                          .split("/")[1]
                          .trim()
                          .replace("頁", "")
                          .trim();
                  pages = Integer.valueOf(pageString);
                }
              }
              break;
            }
          }
          break;
        }
      }

      Book _book = new Book(title);
      _book.setOriginalTile(originalTitle);
      _book.setPublisher(publisher);
      _book.setPublishTime(publishedDateMilli);
      _book.setAuthor(String.join(", ", authors));
      _book.setIsbn13(isbn13);
      _book.setPages(pages);
      _book.setItemUrl(itemUrl);
      return Optional.of(_book);
    } catch (Exception e) {
      Logservice.error(e, this.getClass());
    }

    return Optional.empty();
  }

  private String getItemUrl(String urlSearch) {
    String itemUrl = null;
    try {
      Connection connectionSearch = getConnection(urlSearch);
      Document htmlDocument = connectionSearch.get();
      Element searchList = htmlDocument.getElementById("searchlist");
      Elements items = searchList.getElementsByClass("item");
      if (items.size() != 1) return null;
      Elements h3s = items.get(0).getElementsByTag("h3");
      if (h3s.size() != 1) return null;
      itemUrl = h3s.get(0).getElementsByTag("a").get(0).attr("href");
      itemUrl = getFinalURL(itemUrl);
    } catch (IOException e) {
      Logservice.error(e, this.getClass());
    }
    return itemUrl;
  }

  private Connection getConnection(String url) {
    return Jsoup.connect(url).followRedirects(true).userAgent(USER_AGENT);
  }

  public static String getFinalURL(String url) throws IOException {
    // https://stackoverflow.com/questions/14951696/java-urlconnection-get-the-final-redirected-url
    HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
    con.setInstanceFollowRedirects(false);
    con.connect();
    con.getInputStream();

    if (con.getResponseCode() == HttpURLConnection.HTTP_MOVED_PERM
        || con.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP) {
      String redirectUrl = con.getHeaderField("Location");
      return getFinalURL(redirectUrl);
    }
    return url;
  }
}
