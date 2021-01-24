package org.openapitools.api;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openapitools.entity.MenuItem;
import org.openapitools.entity.MenuItemOption;
import org.openapitools.model.MenuItemDto;
import org.openapitools.repositories.MenuItemOptionRepository;
import org.openapitools.repositories.MenuItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.NativeWebRequest;

import static org.openapitools.framework.ResponseUtility.*;

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2021-01-08T23:22:44.934923-08:00[America/Los_Angeles]")
@Controller
@RequestMapping("${openapi.customerOrders.base-path:}")
public class MenuItemApiController implements MenuItemApi {
  private static final Logger log = LoggerFactory.getLogger(MenuItemApiController.class);

  private final NativeWebRequest request;

  private final ObjectMapper objectMapper;
  
  private final MenuItemRepository menuItemRepository;
  
  private final MenuItemOptionRepository menuItemOptionRepository;

  @org.springframework.beans.factory.annotation.Autowired
  public MenuItemApiController(
      NativeWebRequest request,
      ObjectMapper objectMapper,
      MenuItemRepository menuItemRepository,
      MenuItemOptionRepository menuItemOptionRepository
  ) {
    this.request = request;
    this.objectMapper = objectMapper;
    this.menuItemRepository = menuItemRepository;
    this.menuItemOptionRepository = menuItemOptionRepository;
  }

  @Override
  public Optional<NativeWebRequest> getRequest() {
    return Optional.ofNullable(request);
  }

  @Override
  public ResponseEntity<MenuItemDto> getMenuItem(final Integer id) {
//    logHeaders(request, "MenuItemApiController.getMenuItem(id)");
    return serveOK(() -> {
      MenuItem menuItem = menuItemRepository.getOne(id);
      return objectMapper.convertValue(menuItem, MenuItemDto.class);
    });
  }

  @Override
  public ResponseEntity<List<MenuItemDto>> getAll() {
//    logHeaders(request, "MenuItemApiController.getAll()");
    return serveOK(this::getAllMenuItems);
  }

  private List<MenuItemDto> getAllMenuItems() {
    return menuItemRepository
        .findAll()
        .stream()
        .map((m) -> objectMapper.convertValue(m, MenuItemDto.class))
        .collect(Collectors.toList());
  }

  ////// Package-level methods for unit tests only! //////

  MenuItemOption getMenuItemOptionTestOnly(int id) {
    return menuItemOptionRepository.getOne(id);
  }

  List<MenuItemOption> findAllOptionsTestOnly() {
    return menuItemOptionRepository.findAll();
  }

  MenuItem getMenuItemTestOnly(int id) {
    return menuItemRepository.getOne(id);
  }

}
