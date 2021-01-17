package org.openapitools.repositories;

import java.util.List;
import org.openapitools.OpenAPI2SpringBoot;
import org.openapitools.entity.MenuItemOption;
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
public class MenuItemOptionRepository {
  private final MenuItemOptionUncachedRepository repository;
  private static final String MENU_ITEM_CACHE = OpenAPI2SpringBoot.MENU_ITEM_CACHE;

  @Autowired
  public MenuItemOptionRepository(MenuItemOptionUncachedRepository repo) {
    repository = repo;
  }
  
  @CacheEvict(cacheNames = MENU_ITEM_CACHE)
  public <MIO extends MenuItemOption> MIO save(MIO option) {
    return repository.save(option);
  }
  
  public MenuItemOption getOne(Integer optionId) {
    return repository.getOne(optionId);
  }

  @CacheEvict(cacheNames = MENU_ITEM_CACHE)
  public void delete(MenuItemOption optionToDelete) {
    repository.delete(optionToDelete);
  }
  
  public List<MenuItemOption> findAll() {
    return repository.findAll();
  }
}
