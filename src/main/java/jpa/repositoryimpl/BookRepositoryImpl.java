package jpa.repositoryimpl;

import jpa.domain.Book;
import jpa.repository.BookSearchCriteria;
import jpa.repository.IBookRepository;
import logic.parser.handler.BookParserHandler;
import logic.parser.impl.AmazonBookParser;
import logic.parser.impl.BooksDotComBookParser;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import util.LogService;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;

@Repository
@Transactional
public class BookRepositoryImpl implements IBookRepository {

  @PersistenceContext private EntityManager entityManager;

  @Override
  public List<Book> findAll(BookSearchCriteria searchCriteria, Pageable pageable) {

    Assert.notNull(searchCriteria, "searchCriteria is null");
    Assert.notNull(pageable, "pageable is null");

    int pageNumber = pageable.getPageNumber();
    int pageSize = pageable.getPageSize();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
    countQuery.select(criteriaBuilder.count(countQuery.from(Book.class)));
    Long count = entityManager.createQuery(countQuery).getSingleResult();

    CriteriaQuery<Book> criteriaQuery = criteriaBuilder.createQuery(Book.class);
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
      LogService.errorSaveJson(book, e, this.getClass());
      return false;
    }
    return true;
  }

  @Override
  public boolean updateBook(Book book) {
    try {
      entityManager.merge(book);
    } catch (PersistenceException e) {
      LogService.errorSaveJson(book, e, this.getClass());
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
      LogService.errorSaveJson(book, e, this.getClass());
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
      LogService.debug(e.getStackTrace()[1].getMethodName() + "ISBN = " + isbn);
    }
    return Optional.empty();
  }

  public void findBooks(String[] _isbns, List<String> bookNotFoundList, List<Book> bookList) {
    for (final String ISBN : _isbns) {
      Optional<Book> bookOptional = this.findBookByISBN(ISBN);
      if (bookOptional.isPresent()) {
        bookList.add(bookOptional.get());
        continue;
      }
      BookParserHandler amazonHandler = new AmazonBookParser();
      BookParserHandler bookDotComHandler = new BooksDotComBookParser();
      // Version 1: Chain of Responsibility
      // amazonHandler.setSuccessor(bookDotComHandler);
      // bookOptional = amazonHandler.processRequest(ISBN);

      //Version 2:
      ExecutorService executorService = Executors.newCachedThreadPool();
      List<Callable<Optional<Book>>> callableList = new ArrayList<>();

      callableList.add(() -> amazonHandler.getBook(ISBN));
      callableList.add(() -> bookDotComHandler.getBook(ISBN));
      // end Version 2

      try {
        bookOptional = getBookFromParser(executorService,callableList);
      } catch (InterruptedException e) {
        LogService.error(e,this.getClass());
      }

      if (bookOptional.isPresent()) {
        bookList.add(bookOptional.get());
        this.addBook(bookOptional.get());
      } else {
        bookNotFoundList.add(ISBN);
      }
    }
  }
  /*
  * REF: https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ExecutorCompletionService.html
  * "To use the first non-null result of the set of tasks, ignoring any that encounter exceptions,
  * and cancelling all other tasks when the first one is ready."
  * */
  private Optional<Book> getBookFromParser(Executor e, Collection<Callable<Optional<Book>>> solvers)
      throws InterruptedException {

    CompletionService<Optional<Book>> ecs = new ExecutorCompletionService<>(e);
    int n = solvers.size();
    List<Future<Optional<Book>>> futures = new ArrayList<>(n);
    Book result = null;
    try {
      for (Callable<Optional<Book>> s : solvers) futures.add(ecs.submit(s));
      for (int i = 0; i < n; ++i) {
        try {
          Optional<Book> r = ecs.take().get();
          if (r.isPresent()) {
            result = r.get();
            break;
          }
        } catch (ExecutionException ignore) {
        }
      }
    } finally {
      for (Future<Optional<Book>> f : futures) f.cancel(true);
    }

    return Optional.ofNullable(result);
  }
}
