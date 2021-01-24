package org.openapitools.repositories;

import org.openapitools.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 2/20/18
 * <p>Time: 12:03 AM
 *
 * @author Miguel Mu\u00f1oz
 */
@Repository
public interface MenuItemUncachedRepository extends JpaRepository<MenuItem, Integer> { }
