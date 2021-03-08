package com.neptunedreams.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.neptunedreams.entity.User;

/**
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 1/6/21
 * <p>Time: 12:17 PM
 *
 * @author Miguel Mu\u00f1oz
 */
@Repository
public interface UserRepository extends JpaRepository<User, String> {
  User findByUsername(String username);
  User findByEmail(String email);
  User findByMobilePhone(String mobilePhone);
  User findByLandPhone(String landPhone);
}
