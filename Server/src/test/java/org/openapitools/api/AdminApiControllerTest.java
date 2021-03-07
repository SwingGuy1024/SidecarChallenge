package org.openapitools.api;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import com.neptunedreams.api.AdminApiController;
import org.hibernate.Hibernate;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.neptunedreams.OpenAPI2SpringBoot;
import com.neptunedreams.entity.MenuItem;
import com.neptunedreams.entity.MenuItemOption;
import com.neptunedreams.framework.exception.BadRequest400Exception;
import com.neptunedreams.framework.exception.NotFound404Exception;
import com.neptunedreams.framework.exception.ResponseException;
import com.neptunedreams.model.MenuItemDto;
import com.neptunedreams.model.MenuItemOptionDto;
import com.neptunedreams.repository.MenuItemOptionRepositoryWrapper;
import com.neptunedreams.repository.MenuItemRepositoryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit4.SpringRunner;

import static com.neptunedreams.engine.PojoUtility.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 1/9/21
 * <p>Time: 1:33 PM
 *
 * @author Miguel Mu\u00f1oz
 */
@SuppressWarnings({"CallToNumericToString", "HardCodedStringLiteral", "MagicNumber", "RedundantSuppression"})
@RunWith(SpringRunner.class)
@SpringBootTest(classes = OpenAPI2SpringBoot.class)
@Component
public class AdminApiControllerTest {

  @Autowired
  private AdminApiController adminApiController;

  @Autowired
  private MenuItemRepositoryWrapper menuItemRepositoryWrapper;

  @Autowired
  private MenuItemOptionRepositoryWrapper menuItemOptionRepositoryWrapper;

  @Test(expected = BadRequest400Exception.class)
  public void testAddMenuItemBadInput() {
    MenuItemOptionDto menuItemOption = new MenuItemOptionDto();
    menuItemOption.setName("");
    menuItemOption.setDeltaPrice(new BigDecimal("5.00"));

    MenuItemDto menuItemDto = new MenuItemDto();
    menuItemDto.setAllowedOptions(Collections.singletonList(menuItemOption));
    menuItemDto.setName("BadItem");
    menuItemDto.setItemPrice(new BigDecimal("0.50"));
//    try {
      ResponseEntity<String> responseEntity = adminApiController.addMenuItem(menuItemDto);
      fail(responseEntity.toString());
//    } catch (BadRequest400Exception ignored) { }
  }

  @Test
  public void testAddMenuItemGoodInput() {
    MenuItemDto menuItemDto = makeMenuItem();
    ResponseEntity<String> responseEntity = adminApiController.addMenuItem(menuItemDto);
    assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
    final Integer id = Integer.valueOf(Objects.requireNonNull(responseEntity.getBody()));
    assertNotNull(id);
    MenuItem item = getOneOrThrow(menuItemRepositoryWrapper, id);
    Hibernate.initialize(item);
    assertEquals("0.50", item.getItemPrice().toString());
    assertEquals("GoodItem", item.getName());
    Set<String> foodOptionSet = new HashSet<>();
    Collection<MenuItemOption> optionList = item.getAllowedOptions();
    for (MenuItemOption option : optionList) {
      foodOptionSet.add(option.getName());
    }
    assertThat(foodOptionSet, hasItems("olives", "pepperoni"));
    assertEquals(2, foodOptionSet.size());

  }

  private static MenuItemDto makeMenuItem() {
    MenuItemOptionDto oliveOption = makeMenuItemOptionDto("olives", "0.30");
    MenuItemOptionDto pepOption = makeMenuItemOptionDto("pepperoni", "0.40");

    MenuItemDto menuItemDto = new MenuItemDto();
    menuItemDto.setAllowedOptions(Arrays.asList(oliveOption, pepOption));
    menuItemDto.setName("GoodItem");
    menuItemDto.setItemPrice(new BigDecimal("0.50"));
    return menuItemDto;
  }

  private static MenuItemOptionDto makeMenuItemOptionDto(String name, String price) {
    MenuItemOptionDto option = new MenuItemOptionDto();
    option.setName(name);
    option.setDeltaPrice(new BigDecimal(price));
    return option;
  }

  // Tests of addMenuItemOption()

  @Test
  public void testAddOptionBadInput() {
    isNotFound(5, makeMenuItemOptionDto("olives", "1000.00"));
    isNotFound(6, makeMenuItemOptionDto("pepperoni", "100.00"));
    isBadRequest(5, makeMenuItemOptionDto("", "0.40"));
    isBadRequest(6, makeMenuItemOptionDto("", "0.50"));
  }

  private void isBadRequest(int id, MenuItemOptionDto optionDto) {
    try {
      final ResponseEntity<String> stringResponseEntity = adminApiController.addMenuItemOption(id, optionDto);
      fail(stringResponseEntity.toString());
    } catch (BadRequest400Exception ignored) { }
  }

  private void isNotFound(int id, MenuItemOptionDto optionDto) {
    try {
      final ResponseEntity<String> stringResponseEntity = adminApiController.addMenuItemOption(id, optionDto);
      fail(stringResponseEntity.toString());
    } catch (NotFound404Exception ignored) { }
  }

  // Test of deleteOption()

  @Test
  public void testDeleteOption() throws ResponseException {
    MenuItemDto menuItemDto = createPizzaMenuItem();
    ResponseEntity<String> responseEntity = adminApiController.addMenuItem(menuItemDto);
    final Integer id = Integer.valueOf(Objects.requireNonNull(responseEntity.getBody()));
    assertNotNull(id);
    System.out.printf("Body: <%s>%n", id);

    MenuItem item = getOneOrThrow(menuItemRepositoryWrapper, id);
    Hibernate.initialize(item);
    List<String> nameList = new LinkedList<>();
    for (MenuItemOption option : item.getAllowedOptions()) {
      nameList.add(option.getName());
    }
    assertThat(nameList, hasItems("pepperoni", "sausage", "mushrooms", "bell peppers", "olives", "onions"));


    try {
      ResponseEntity<Void> badResponseTwo = adminApiController.deleteOption(100000);
      fail(badResponseTwo.toString());
    } catch (NotFound404Exception ignored) { }

    MenuItemOption removedOption = item.getAllowedOptions().iterator().next();
    String removedName = removedOption.getName();
    Integer removedId = removedOption.getId();
    assertNotNull(removedId);
    assertTrue(hasName(item, removedName));
    assertNotNull(getOneOrThrow(menuItemOptionRepositoryWrapper, removedId));
    ResponseEntity<Void> goodResponse = adminApiController.deleteOption(removedOption.getId());

    assertEquals(HttpStatus.OK, goodResponse.getStatusCode());

    List<MenuItemOption> allOptions = menuItemOptionRepositoryWrapper.findAll();
    for (MenuItemOption option : allOptions) {
      System.out.println(option);
    }

    item = getOneOrThrow(menuItemRepositoryWrapper, id);
    assertFalse(hasName(item, removedName));
    try {
      getOneOrThrow(menuItemOptionRepositoryWrapper, removedId);
      fail("Item not removed");
    } catch (NotFound404Exception ignored) { }
  }

  public static MenuItemDto createPizzaMenuItem() {
    MenuItemDto menuItemDto = new MenuItemDto();
    menuItemDto.setName("Pizza");
    menuItemDto.setAllowedOptions(new LinkedList<>());
    menuItemDto.getAllowedOptions().add(makeMenuItemOptionDto("pepperoni", "0.30"));
    menuItemDto.getAllowedOptions().add(makeMenuItemOptionDto("sausage", "0.30"));
    menuItemDto.getAllowedOptions().add(makeMenuItemOptionDto("mushrooms", "0.15"));
    menuItemDto.getAllowedOptions().add(makeMenuItemOptionDto("bell peppers", "0.15"));
    menuItemDto.getAllowedOptions().add(makeMenuItemOptionDto("olives", "0.00"));
    menuItemDto.getAllowedOptions().add(makeMenuItemOptionDto("onions", "0.00"));
    menuItemDto.setItemPrice(new BigDecimal("5.95"));
    return menuItemDto;
  }

  private static boolean hasName(MenuItem item, String optionName) {
    for (MenuItemOption option : item.getAllowedOptions()) {
      if (Objects.equals(option.getName(), optionName)) {
        return true;
      }
    }
    return false;
  }

  @After
  public void tearDown() {
    List<MenuItem> menuItems = menuItemRepositoryWrapper.findAll();
    for (MenuItem menuItem : menuItems) {
      Collection<MenuItemOption> ops = menuItem.getAllowedOptions();
      menuItem.setAllowedOptions(new LinkedList<>());
      menuItemRepositoryWrapper.save(menuItem);
      menuItemOptionRepositoryWrapper.deleteInBatch(ops);
    }
    menuItemRepositoryWrapper.deleteInBatch(menuItems);

    List<MenuItemOption> optionList = menuItemOptionRepositoryWrapper.findAll();
    menuItemOptionRepositoryWrapper.deleteInBatch(optionList);
  }
}