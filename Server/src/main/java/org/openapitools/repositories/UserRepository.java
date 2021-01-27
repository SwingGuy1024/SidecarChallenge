package org.openapitools.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.openapitools.entity.User;

/**
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 1/6/21
 * <p>Time: 12:17 PM
 *
 * @author Miguel Mu\u00f1oz
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
  User findByUsername(String username);
}