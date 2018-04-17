package logic.parser.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jpa.domain.Book;
import logic.parser.Hendler.BookParserHandler;
import logic.parser.IBookParser;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.Locale;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

public class AmazonBookParserTest {

    @Test
    public void getBook() throws ParseException {
        BookParserHandler iBookParser = new AmazonBookParser();
        Optional<Book> bookParsed = iBookParser.getBook("9780375725784");

        Book book_correct = new Book("A Heartbreaking Work of Staggering Genius");
        book_correct.setIsbn13("9780375725784");
        book_correct.setIsbn10("0375725784");
        book_correct.setPages(437);
        book_correct.setAuthor("Dave Eggers");
        book_correct.setPublisher("Vintage");

        SimpleDateFormat format = new SimpleDateFormat("MMMM dd, yyyy", Locale.US);
        Instant instant = format.parse("February 13, 2001").toInstant();
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