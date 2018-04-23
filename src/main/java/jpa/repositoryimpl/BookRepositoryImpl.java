package jpa.repositoryimpl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jpa.domain.Book;
import jpa.repository.BookSearchCriteria;
import jpa.repository.IBookRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import util.Logservice;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class BookRepositoryImpl implements IBookRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Book> findAll(BookSearchCriteria searchCriteria, Pageable pageable) {

        Assert.notNull(searchCriteria, "searchCriteria is null");
        Assert.notNull(pageable, "pageable is null");

        int pageNumber = pageable.getPageNumber();
        int pageSize = pageable.getPageSize();

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = criteriaBuilder
                .createQuery(Long.class);
        countQuery.select(criteriaBuilder
                .count(countQuery.from(Book.class)));
        Long count = entityManager.createQuery(countQuery)
                .getSingleResult();

        CriteriaQuery<Book> criteriaQuery = criteriaBuilder
                .createQuery(Book.class);
        Root<Book> from = criteriaQuery.from(Book.class);
        CriteriaQuery<Book> select = criteriaQuery.select(from);

        TypedQuery<Book> typedQuery = entityManager.createQuery(select);

        typedQuery.setFirstResult(pageNumber - 1);
        typedQuery.setMaxResults(pageSize);

        return typedQuery.getResultList();
    }

    @Override
    public boolean addBook(Book book) {
        try {
            entityManager.persist(book);
        } catch (PersistenceException e) {
          Logservice.errorSaveJson(book, e, this.getClass());
          return false;
        }
        return true;
    }

    @Override
    public boolean updateBook(Book book) {
        try {
          entityManager.merge(book);
        } catch (PersistenceException e) {
          Logservice.errorSaveJson(book, e, this.getClass());
          return false;
        }
        return true;
    }

    @Override
    public boolean deleteBook(Book book) {
        try {
            Book _book = entityManager.find(Book.class, book.getId());
            entityManager.remove(_book);
        } catch (PersistenceException e) {
          Logservice.errorSaveJson(book, e, this.getClass());
          return false;
        }
        return true;
    }

  @Override
  public Optional<Book> findBookByISBN(String isbn) {
    try {
      TypedQuery<Book> query =
          entityManager.createQuery(
              "SELECT b FROM Book b WHERE b.isbn13 = :isbn or b.isbn10 = :isbn", Book.class);
      return Optional.ofNullable(query.setParameter("isbn", isbn).getSingleResult());
    } catch (NoResultException e) {
      Logservice.errorSaveJson(isbn, e, this.getClass());
    }
    return Optional.empty();
  }
}
