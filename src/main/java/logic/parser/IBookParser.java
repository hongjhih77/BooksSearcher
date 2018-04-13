package logic.parser;

import jpa.domain.Book;

public interface IBookParser {
    Book getBook(String key);
}
