package org.openapitools.repositories;

import org.openapitools.framework.exception.NotFound404Exception;
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
abstract class RepositoryWrapper<T, ID> {
  private final JpaRepository<T, ID> repository;

  protected RepositoryWrapper(JpaRepository<T, ID> repository) {
    this.repository = repository;
  }
  
  protected final JpaRepository<T, ID> getRepo() { return repository; }

  /**
   * This method tests the object for existence and throws an exception if it's not in the table.
   * @param id The id
   * @return The entity with the provided id
   */
  public T getOne(ID id) {
    if (repository.existsById(id)) {
      return repository.getOne(id);
    }
    throw new NotFound404Exception("Missing object");
  }
}
