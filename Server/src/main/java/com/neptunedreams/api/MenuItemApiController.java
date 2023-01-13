package com.neptunedreams.api;

import java.util.List;
import java.util.Optional;
import com.neptunedreams.service.MenuService;
import com.neptunedreams.model.MenuItemDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.NativeWebRequest;

import static com.neptunedreams.framework.ResponseUtility.*;

@Controller
@RequestMapping("${openapi.customerOrders.base-path:}")
public class MenuItemApiController implements MenuItemApi {
  private static final Logger log = LoggerFactory.getLogger(MenuItemApiController.class);

  private final NativeWebRequest request;

  private final MenuService menuService;
  
  @Autowired
  public MenuItemApiController(
      @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
      NativeWebRequest request,
      MenuService menuService
  ) {
    this.request = request;
    this.menuService = menuService;
  }

  @Override
  public Optional<NativeWebRequest> getRequest() {
    return Optional.ofNullable(request);
  }

  @Override
  public ResponseEntity<MenuItemDto> getMenuItem(final Integer id) {
//    logHeaders(request, "MenuItemApiController.getMenuItem(id)");
    return serveOK(() -> menuService.getMenuItemDto(id));
  }

  @Override
  public ResponseEntity<List<MenuItemDto>> getAll() {
//    logHeaders(request, "MenuItemApiController.getAll()");
    return serveOK(menuService::getAllMenuItems);
  }
}
