package logic.parser.Hendler;

import jpa.domain.Book;

import java.util.Optional;

abstract  public class BookParserHandler  {
  protected BookParserHandler successor;

  public void setSuccessor(BookParserHandler successor) {
    this.successor = successor;
  }

  public Optional<Book> processRequest(String key) {
    Optional<Book> book = getBook(key);
    if (!book.isPresent() && successor != null) {
      return successor.processRequest(key);
    }else {
      return book;
    }
  }

  abstract public Optional<Book> getBook(String isbn);
}
