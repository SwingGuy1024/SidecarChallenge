package com.neptunedreams.api;

import java.util.Optional;
import javax.validation.Valid;
import com.neptunedreams.service.MenuService;
import com.neptunedreams.userservice.Role;
import com.neptunedreams.userservice.UserService;
import com.neptunedreams.framework.ResponseUtility;
import com.neptunedreams.model.MenuItemDto;
import com.neptunedreams.model.MenuItemOptionDto;
import com.neptunedreams.model.UserDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;

import static com.neptunedreams.framework.ResponseUtility.*;

@RestController
@RequestMapping("${openapi.customerOrders.base-path:}")
public class AdminApiController implements AdminApi {

    private static final Logger log = LoggerFactory.getLogger(AdminApiController.class);

    private final NativeWebRequest request;
    
    private final MenuService menuService;
    
    private final UserService userService;

    @Autowired
    public AdminApiController(
        final MenuService menuService,
        final UserService userService,
        @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
        final NativeWebRequest request
    ) {
        this.request = request;
        this.menuService = menuService;
        this.userService = userService;
        log.trace("instantiating AdminApiController");
    }

    // I would never wrap this in an Optional, because it's never null, but the interface is generated.
    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);
    }

    @Override
    public ResponseEntity<String> addMenuItemOption(final Integer menuItemId, final MenuItemOptionDto optionDto) {
        log.trace("addMenuItemOption() to id {}: {}", menuItemId, optionDto);
//        logHeaders(request, "AdminApiController addMenuItemOption");
        return serveCreatedEntity(() -> menuService.addOption(menuItemId, optionDto));
    }

    @Override
    public ResponseEntity<String> addMenuItem(final MenuItemDto menuItemDto) {
        log.trace("addMenuItem: {}", menuItemDto);
        return serveCreatedEntity(() -> menuService.addMenuItemFromDto(menuItemDto));
    }

    @Override
    public ResponseEntity<String> addNewMenuItemOption(@Valid final MenuItemOptionDto menuItemOptionDto) {
        log.trace("addNewMenuItemOption(): {}", menuItemOptionDto);
        return serveCreatedEntity(() -> menuService.createNewOption(menuItemOptionDto));
    }

    @Override
    public ResponseEntity<Void> deleteOption(final Integer optionId) {
//        logHeaders(request, "AdminApiController deleteOption");
        return serveOK(() -> menuService.deleteById(optionId));
    }

    @Override
    public ResponseEntity<Void> addAdmin(@Valid final UserDto userDto) {
        return ResponseUtility.serveOK(() -> userService.createUser(userDto, Role.ADMIN));
    }
}
