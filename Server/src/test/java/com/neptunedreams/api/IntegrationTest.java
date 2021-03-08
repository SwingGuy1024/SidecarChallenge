package com.neptunedreams.api;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import com.neptunedreams.OpenAPI2SpringBoot;
import com.neptunedreams.engine.DataEngine;
import com.neptunedreams.entity.MenuItem;
import com.neptunedreams.entity.MenuItemOption;
import com.neptunedreams.model.MenuItemDto;
import com.neptunedreams.model.MenuItemOptionDto;
import com.neptunedreams.repository.MenuItemOptionRepositoryWrapper;
import com.neptunedreams.repository.MenuItemRepositoryWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

/**
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 2/9/21
 * <p>Time: 1:59 AM
 *
 * @author Miguel Mu\u00f1oz
 */
@SuppressWarnings("HardcodedLineSeparator")

@ComponentScan(basePackages = {
    "org.openapitools",
    "com.neptunedreams.api",
    "com.neptunedreams.server",
    "com.neptunedreams.repository",
    "com.neptunedreams.framework.util",
    "com.neptunedreams.engine",
    "org.openapitools.configuration"
})
@ExtendWith(SpringExtension.class)
@RunWith(SpringRunner.class)
@SpringBootTest(classes = OpenAPI2SpringBoot.class)
@Component
public class IntegrationTest {
  private static final Logger log = LoggerFactory.getLogger(IntegrationTest.class);
  private static final ObjectMapper objectMapper = new ObjectMapper();

  @Autowired
  private MenuItemRepositoryWrapper menuItemRepositoryWrapper;

  @Autowired
  private MenuItemOptionRepositoryWrapper menuItemOptionRepositoryWrapper;

  @Autowired
  private DataEngine dataEngine;
  
  @Test
  public void buildMenuTest() throws JsonProcessingException {
    assertNotNull(menuItemRepositoryWrapper);
    assertNotNull(menuItemOptionRepositoryWrapper);
    assertNotNull(dataEngine);

    MenuItemDto caesarItemDto = buildMenuItem(LARGE_CAESAR_SALAD);
    log.debug("Large Caesar Salad: \n{}", LARGE_CAESAR_SALAD);

    Integer saladId = dataEngine.addMenuItemFromDto(caesarItemDto);
    
    MenuItemDto pizzaItemDto = buildMenuItem(PIZZA_14_INCH);
    log.debug("Pizza - 14 inch\n{}", PIZZA_14_INCH);
    Integer pizzaId = dataEngine.addMenuItemFromDto(pizzaItemDto);

    MenuItemOptionDto onionsDto = buildMenuItemOption(ONIONS_OPTION);
    Integer onionId = dataEngine.addOption(pizzaId, onionsDto);
    
    MenuItemOptionDto anchoviesDto = buildMenuItemOption(ANCHOVIES_OPTION);
    Integer anchoviesId = dataEngine.addOption(pizzaId, anchoviesDto);
    
    List<MenuItemDto> allItems = dataEngine.getAllMenuItems();
    System.out.println(allItems);
    Assert.assertThat(allItems, Matchers.hasSize(2));
    
    findById(saladId, allItems);
    MenuItemDto pizza = findById(pizzaId, allItems);
    assertEquals(pizzaId, pizza.getId());
    
    MenuItemOptionDto onions = findById(onionId, pizza);
    assertEquals(onionId, onions.getId());
    MenuItemOptionDto anchovies = findById(anchoviesId, pizza);
    assertEquals(anchoviesId, anchovies.getId());
  }
  
  private MenuItemDto findById(int id, Collection<MenuItemDto> menuItemDtos) {
    for (MenuItemDto dto: menuItemDtos) {
      if (dto.getId() == id) {
        return dto;
      }
    }
    throw new AssertionError(String.format("MenuItemDto with id %d not found", id));
  }
  
  private MenuItemOptionDto findById(int id, MenuItemDto menuItem) {
    for (MenuItemOptionDto dto: menuItem.getAllowedOptions()) {
      if (dto.getId() == id) {
        return dto;
      }
    }
    throw new AssertionError(String.format("MenuItemOptionDto with id %d not found", id));
  }
  
  private static MenuItemDto buildMenuItem(String json) throws JsonProcessingException {
    return objectMapper.readValue(json, MenuItemDto.class);
  }
  
  private static MenuItemOptionDto buildMenuItemOption(String json) throws JsonProcessingException {
    return objectMapper.readValue(json, MenuItemOptionDto.class);
  }

  private static final String LARGE_CAESAR_SALAD = 
      "{\n" +
      "    \"name\": \"Large Caesar Salad\",\n" +
      "    \"itemPrice\": 10.50,\n" +
      "    \"allowedOptions\": [\n" +
      "        {\n" +
      "            \"name\": \"Ranch Dressing\",\n" +
      "            \"deltaPrice\": 0.00\n" +
      "        }\n" +
      "    ]\n" +
      '}';
  
  public static final String PIZZA_14_INCH =
      "{\n" +
      "    \"name\": \"14 inch pizza\",\n" +
      "    \"itemPrice\": 12.50,\n" +
      "    \"allowedOptions\": [\n" +
      "        {\n" +
      "            \"name\": \"Pepperoni\",\n" +
      "            \"deltaPrice\": 0.50\n" +
      "        },\n" +
      "        {\n" +
      "            \"name\": \"Shrooms\",\n" +
      "            \"deltaPrice\": 0.50\n" +
      "        },\n" +
      "        {\n" +
      "            \"name\": \"Sausage\",\n" +
      "            \"deltaPrice\": 0.50\n" +
      "        },\n" +
      "        {\n" +
      "            \"name\": \"Olives\",\n" +
      "            \"deltaPrice\": 0.50\n" +
      "        }\n" +
      "    ]\n" +
      '}';
  
  public static final String ONIONS_OPTION =
      "{\n" +
      "    \"name\": \"Onions\",\n" +
      "    \"deltaPrice\": 0.50\n" +
      '}';

  public static final String ANCHOVIES_OPTION =
      "{\n" +
          "    \"name\": \"Anchovies\",\n" +
          "    \"deltaPrice\": 1.00\n" +
          '}';

  @After
  public void tearDown() {
    List<MenuItem> menuItems = menuItemRepositoryWrapper.findAll();
    for (MenuItem menuItem: menuItems) {
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
