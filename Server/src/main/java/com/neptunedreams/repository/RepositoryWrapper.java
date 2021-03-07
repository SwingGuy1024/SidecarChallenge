package com.neptunedreams.repository;

import java.util.List;
import java.util.Optional;
import com.neptunedreams.framework.exception.NotFound404Exception;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Because we want to do caching at the repo level. and because we can only annotate concrete methods, this class exists to
 * wrap a JpaRepository instance, which only has abstract methods, so we can create concrete methods to annotate. See subclasses
 * for examples.
 * <p>
 * This also provides a cleaner way to test entities for existence by id, throwing the proper exception if they are not found. 
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 1/20/21
 * <p>Time: 11:05 PM
 *
 * @author Miguel Mu\u00f1oz
 */
class RepositoryWrapper<T, ID> implements JpaRepository<T, ID> {
  private final JpaRepository<T, ID> repository;

  protected RepositoryWrapper(JpaRepository<T, ID> repository) {
    this.repository = repository;
  }
  
  protected final JpaRepository<T, ID> getRepo() { return repository; }

  @Override
  public T getOne(final ID id) {
    return repository.getOne(id);
  }

  @Override
  public void deleteInBatch(Iterable<T> items) {
    repository.deleteInBatch(items);
  }

  @Override
  public List<T> findAll() {
    return repository.findAll();
  }

  @Override
  public List<T> findAll(final Sort sort) {
    return repository.findAll(sort);
  }

  @Override
  public List<T> findAllById(final Iterable<ID> iterable) {
    return repository.findAllById(iterable);
  }

  @Override
  public <S extends T> List<S> saveAll(final Iterable<S> iterable) {
    return repository.saveAll(iterable);
  }

  @Override
  public void flush() {
    repository.flush();
  }

  @Override
  public <S extends T> S saveAndFlush(final S s) {
    return repository.saveAndFlush(s);
  }

  @Override
  public void deleteAllInBatch() {
    repository.deleteAllInBatch();
  }

  @Override
  public <S extends T> List<S> findAll(final Example<S> example) {
    return repository.findAll(example);
  }

  @Override
  public <S extends T> List<S> findAll(final Example<S> example, final Sort sort) {
    return repository.findAll(example, sort);
  }

  @Override
  public Page<T> findAll(final Pageable pageable) {
    return repository.findAll(pageable);
  }

  @Override
  public <S extends T> S save(final S entity) {
    return repository.save(entity);
  }

  @Override
  public Optional<T> findById(final ID id) {
    return repository.findById(id);
  }

  @Override
  public boolean existsById(final ID id) {
    return repository.existsById(id);
  }

  @Override
  public long count() {
    return repository.count();
  }

  @Override
  public void deleteById(final ID id) {
    repository.deleteById(id);
  }

  @Override
  public void delete(final T entity) {
    repository.delete(entity);
  }

  @Override
  public void deleteAll(final Iterable<? extends T> entities) {
    repository.deleteAll(entities);
  }

  @Override
  public void deleteAll() {
    repository.deleteAll();
  }

  @Override
  public <S extends T> Optional<S> findOne(final Example<S> example) {
    return repository.findOne(example);
  }

  @Override
  public <S extends T> Page<S> findAll(final Example<S> example, final Pageable pageable) {
    return repository.findAll(example, pageable);
  }

  @Override
  public <S extends T> long count(final Example<S> example) {
    return repository.count(example);
  }

  @Override
  public <S extends T> boolean exists(final Example<S> example) {
    return repository.exists(example);
  }
}
