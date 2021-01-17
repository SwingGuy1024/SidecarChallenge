package org.openapitools.repositories;

import java.util.List;
import org.openapitools.OpenAPI2SpringBoot;
import org.openapitools.entity.MenuItem;
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
public class MenuItemRepository {
  private static final Logger log = LoggerFactory.getLogger(MenuItemRepository.class);
  private final MenuItemUncachedRepository menuItemRepository;
  private static final String MENU_ITEM_CACHE = OpenAPI2SpringBoot.MENU_ITEM_CACHE;

  @Autowired
  public MenuItemRepository(MenuItemUncachedRepository repository) {
    menuItemRepository = repository;
  }

  public MenuItem findOne(Integer id) {
    return menuItemRepository.findOne(id);
  }

  @Cacheable(cacheNames = MENU_ITEM_CACHE, key = "all")
  public List<MenuItem> findAll() {
    log.debug("getAll from cache");
    return menuItemRepository.findAll();
  }

  @CacheEvict(cacheNames = MenuItemRepository.MENU_ITEM_CACHE)
  public <M extends MenuItem> M save(M menuItem) {
    return menuItemRepository.save(menuItem);
  }
}
