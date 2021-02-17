package org.openapitools.engine;

import java.util.List;
import java.util.stream.Collectors;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openapitools.entity.MenuItem;
import org.openapitools.entity.MenuItemOption;
import org.openapitools.framework.exception.BadRequest400Exception;
import org.openapitools.model.MenuItemDto;
import org.openapitools.model.MenuItemOptionDto;
import org.openapitools.repositories.MenuItemOptionRepositoryWrapper;
import org.openapitools.repositories.MenuItemRepositoryWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.openapitools.engine.PojoUtility.*;

/**
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 2/9/21
 * <p>Time: 12:19 AM
 *
 * @author Miguel Mu\u00f1oz
 */
@Component
public class DataEngine {
  private static final Logger log = LoggerFactory.getLogger(DataEngine.class);
  private final MenuItemRepositoryWrapper menuItemRepositoryWrapper;
  private final MenuItemOptionRepositoryWrapper menuItemOptionRepositoryWrapper;
  private final ObjectMapper objectMapper;

  @Autowired
  public DataEngine(
      final MenuItemRepositoryWrapper menuItemRepositoryWrapper,
      final MenuItemOptionRepositoryWrapper menuItemOptionRepositoryWrapper,
      final ObjectMapper objectMapper
  ) {
    this.menuItemRepositoryWrapper = menuItemRepositoryWrapper;
    this.menuItemOptionRepositoryWrapper = menuItemOptionRepositoryWrapper;
    this.objectMapper = objectMapper;
  }

  public MenuItemDto getMenuItemDto(final Integer id) {
    MenuItem menuItem = menuItemRepositoryWrapper.getOne(id);
    return objectMapper.convertValue(menuItem, MenuItemDto.class);
  }

  public List<MenuItemDto> getAllMenuItems() {
    return menuItemRepositoryWrapper
        .findAll()
        .stream()
        .map((m) -> objectMapper.convertValue(m, MenuItemDto.class))
        .collect(Collectors.toList());
  }

  public Integer addOption(final Integer menuItemId, final MenuItemOptionDto optionDto) {
    confirmNotEmpty(optionDto.getName()); // throws ResponseException
    MenuItemOption menuItemOption = objectMapper.convertValue(optionDto, MenuItemOption.class);
    final MenuItem menuItem = menuItemRepositoryWrapper.getOneOrThrow(menuItemId);
    menuItemOption.setMenuItem(menuItem);
    MenuItemOption savedOption = menuItemOptionRepositoryWrapper.save(menuItemOption);
    return savedOption.getId();
  }

  public Integer addMenuItemFromDto(final MenuItemDto menuItemDto) {
    for (MenuItemOptionDto option : skipNull(menuItemDto.getAllowedOptions())) {
      final String optionName = option.getName();
      if ((optionName == null) || optionName.isEmpty()) {
        throw new BadRequest400Exception("Missing Food Option name for item");
      }
    }
    MenuItem menuItem = convertMenuItem(menuItemDto);
    log.trace("MenuItem: {}", menuItem);
    MenuItem savedItem = menuItemRepositoryWrapper.save(menuItem);
    final Integer id = savedItem.getId();
    log.trace("added menuItem with id {}", id);
    return id;
  }

  public Integer createNewOption(final MenuItemOptionDto menuItemOptionDto) {
    MenuItemOption menuItemOption = convertMenuItemOption(menuItemOptionDto);
    log.trace("MenuItemOption: {}", menuItemOption);
    MenuItemOption savedItem = menuItemOptionRepositoryWrapper.save(menuItemOption);
    final Integer id = savedItem.getId();
    log.trace("MenuItemOption added with id {}", id);
    return id;
  }

  private MenuItem convertMenuItem(final MenuItemDto menuItemDto) {
    final MenuItem menuItem = objectMapper.convertValue(menuItemDto, MenuItem.class);

    // objectMapper doesn't set the menuItems in the options, because it can't handle circular references, so we
    // set them here.
    for (MenuItemOption option : menuItem.getAllowedOptions()) {
      option.setMenuItem(menuItem);
    }

    log.trace("Menu Item {}", menuItem);

    return menuItem;
  }

  public Void deleteById(final Integer optionId) {
    log.trace("Deleting menuItemOption with id {}", optionId);


    MenuItemOption itemToDelete = menuItemOptionRepositoryWrapper.getOneOrThrow(optionId);

    // Before I can successfully delete the menuItemOption, I first have to set its menuItem to null. If I don't
    // do that, the delete call will fail. It doesn't help to set Cascade to Remove in the @ManyToOne annotation in 
    // MenuItemOption. Since it's set to ALL in MenuItem's @OneToMany annotation, the Cascade value doesn't seem to 
    // affect this.
    itemToDelete.setMenuItem(null);
    menuItemOptionRepositoryWrapper.save(itemToDelete);

    menuItemOptionRepositoryWrapper.delete(itemToDelete);
    return null;
  }

  private MenuItemOption convertMenuItemOption(final MenuItemOptionDto menuItemOptionDto) {
    return objectMapper.convertValue(menuItemOptionDto, MenuItemOption.class);
  }
}