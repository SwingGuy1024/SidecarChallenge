package com.neptunedreams.engine;

import java.util.List;
import java.util.stream.Collectors;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neptunedreams.entity.MenuItem;
import com.neptunedreams.entity.MenuItemOption;
import com.neptunedreams.exception.BadRequest400Exception;
import com.neptunedreams.model.MenuItemDto;
import com.neptunedreams.model.MenuItemOptionDto;
import com.neptunedreams.repository.MenuItemOptionRepositoryWrapper;
import com.neptunedreams.repository.MenuItemRepositoryWrapper;
import org.jetbrains.annotations.NonNls;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.neptunedreams.engine.PojoUtility.*;

/**
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 2/9/21
 * <p>Time: 12:19 AM
 *
 * @author Miguel Mu\u00f1oz
 */
@Component
public class DataEngine {
  private static final @NonNls Logger log = LoggerFactory.getLogger(DataEngine.class);
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
    MenuItem menuItem = confirmFound(menuItemRepositoryWrapper, id);
    return objectMapper.convertValue(menuItem, MenuItemDto.class);
  }

  public List<MenuItemDto> getAllMenuItems() {
    return menuItemRepositoryWrapper
        .findAll()
        .stream()
        .map(m -> objectMapper.convertValue(m, MenuItemDto.class))
        .collect(Collectors.toList());
  }

  public Integer addOption(final Integer menuItemId, final MenuItemOptionDto optionDto) {
    confirmNotEmpty(optionDto.getName()); // throws ResponseException
    MenuItemOption menuItemOption = objectMapper.convertValue(optionDto, MenuItemOption.class);
    final MenuItem menuItem = confirmFound(menuItemRepositoryWrapper, menuItemId);
    menuItemOption.setMenuItem(menuItem);
    MenuItemOption savedOption = menuItemOptionRepositoryWrapper.save(menuItemOption);
    final Integer newId = savedOption.getId();
    assert newId != null;
    return newId;
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
    final Integer newId = savedItem.getId();
    assert newId != null;
    log.trace("MenuItemOption added with ID {}", newId);
    return newId;
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


    MenuItemOption itemToDelete = confirmFound(menuItemOptionRepositoryWrapper, optionId);

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
