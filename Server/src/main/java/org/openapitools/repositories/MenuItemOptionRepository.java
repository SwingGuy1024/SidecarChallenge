package org.openapitools.repositories;

import org.openapitools.entity.MenuItemOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 2/20/18
 * <p>Time: 1:57 AM
 *
 * @author Miguel Mu\u00f1oz
 */
@Repository
interface MenuItemOptionRepository extends JpaRepository<MenuItemOption, Integer> { }
