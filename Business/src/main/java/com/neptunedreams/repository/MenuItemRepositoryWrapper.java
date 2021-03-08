package com.neptunedreams.repository;

import java.util.List;
import com.neptunedreams.entity.MenuItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

/**
 * Drop-in replacement for a MenuItem repository, to add support for caching. This delegates all work to the actual repository, which
 * is not a public class. This lets me add caching annotations to the methods.
 * See https://docs.spring.io/spring-framework/docs/current/reference/html/integration.html#cache
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 1/16/21
 * <p>Time: 11:37 PM
 *
 * @author Miguel Mu\u00f1oz
 */
@Component
public class MenuItemRepositoryWrapper extends RepositoryWrapper<MenuItem, Integer> {
  private static final Logger log = LoggerFactory.getLogger(MenuItemRepositoryWrapper.class);
  private static final String MENU_ITEM_CACHE = MenuItemRepository.MENU_ITEM_CACHE;

  @Autowired
  public MenuItemRepositoryWrapper(MenuItemRepository repository) {
    super(repository);
  }

  @Override
  @Cacheable(cacheNames = MENU_ITEM_CACHE)
  public List<MenuItem> findAll() {
    return super.findAll();
  }

  @Override
  @CacheEvict(cacheNames = MenuItemRepositoryWrapper.MENU_ITEM_CACHE, allEntries = true)
  public <M extends MenuItem> M save(M menuItem) {
    log.trace("Saving MenuItem {}", menuItem);
    return super.save(menuItem);
  }
}
