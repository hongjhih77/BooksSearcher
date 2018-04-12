package jpa.repositoryimpl;

import jpa.domain.Book;
import jpa.repository.BookSearchCriteria;
import jpa.repository.IBookRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

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
            //TODO Log
            return false;
        }
        return true;
    }

    @Override
    public boolean updateBook(Book book) {
        try {
            entityManager.merge(book);
        } catch (PersistenceException e) {
            //TODO Log
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
            //TODO Log
            return false;
        }
        return true;
    }
}
