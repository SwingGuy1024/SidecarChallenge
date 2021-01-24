package org.openapitools.repositories;

import java.util.List;
import org.openapitools.OpenAPI2SpringBoot;
import org.openapitools.entity.MenuItemOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Component;

/**
 * Wraps the real (package-access MenuItemRepository with delegator methods, allowing us to add caching annotations
 * to the repository calls.
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 1/17/21
 * <p>Time: 1:25 AM
 *
 * @author Miguel Mu\u00f1oz
 */
@Component
public class MenuItemOptionRepository extends RepositoryWrapper<MenuItemOption, Integer> {
  private static final Logger log = LoggerFactory.getLogger(MenuItemOptionRepository.class);
  private static final String MENU_ITEM_CACHE = OpenAPI2SpringBoot.MENU_ITEM_CACHE;

  @Autowired
  public MenuItemOptionRepository(MenuItemOptionUncachedRepository repo) {
    super(repo);
  }
  
  @CacheEvict(cacheNames = MENU_ITEM_CACHE, allEntries = true)
  public <MIO extends MenuItemOption> MIO save(MIO option) {
    log.trace("Save menu item option {}", option);
    return getRepo().save(option);
  }
  
  @CacheEvict(cacheNames = MENU_ITEM_CACHE, allEntries = true)
  public void delete(MenuItemOption optionToDelete) {
    getRepo().delete(optionToDelete);
  }
  
  public List<MenuItemOption> findAll() {
    return getRepo().findAll();
  }
}
