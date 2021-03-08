package com.neptunedreams.repository;

import com.neptunedreams.entity.MenuItemOption;
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
public class MenuItemOptionRepositoryWrapper extends RepositoryWrapper<MenuItemOption, Integer> {
  private static final Logger log = LoggerFactory.getLogger(MenuItemOptionRepositoryWrapper.class);
  private static final String MENU_ITEM_CACHE = MenuItemRepository.MENU_ITEM_CACHE;

  @Autowired
  public MenuItemOptionRepositoryWrapper(MenuItemOptionRepository repo) {
    super(repo);
  }

  @Override
  @CacheEvict(cacheNames = MENU_ITEM_CACHE, allEntries = true)
  public <MIO extends MenuItemOption> MIO save(MIO option) {
    log.trace("Save menu item option {}", option);
    return super.save(option);
  }

  @Override
  @CacheEvict(cacheNames = MENU_ITEM_CACHE, allEntries = true)
  public void delete(MenuItemOption optionToDelete) {
    super.delete(optionToDelete);
  }

}
