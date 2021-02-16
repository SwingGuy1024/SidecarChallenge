package org.openapitools.api;

import java.util.Optional;
import javax.validation.Valid;
import org.openapitools.engine.DataEngine;
import org.openapitools.engine.Role;
import org.openapitools.engine.UserEngine;
import org.openapitools.framework.ResponseUtility;
import org.openapitools.model.CreatedResponse;
import org.openapitools.model.MenuItemDto;
import org.openapitools.model.MenuItemOptionDto;
import org.openapitools.model.UserDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;

import static org.openapitools.framework.ResponseUtility.*;

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2021-01-08T23:22:44.934923-08:00[America/Los_Angeles]")
@RestController
@RequestMapping("${openapi.customerOrders.base-path:}")
public class AdminApiController implements AdminApi {

    private static final Logger log = LoggerFactory.getLogger(AdminApiController.class);

    private final NativeWebRequest request;
    
    private DataEngine dataEngine;
    
    private UserEngine userEngine;

    @Autowired
    public AdminApiController(
        final DataEngine dataEngine,
        final UserEngine userEngine,
        final NativeWebRequest request
    ) {
        this.request = request;
        this.dataEngine = dataEngine;
        log.trace("instantiating AdminApiController");
    }

    // I would never wrap this in an Optional, because it's never null, but the interface is generated.
    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);
    }

    @Override
    public ResponseEntity<CreatedResponse> addMenuItemOption(final Integer menuItemId, final MenuItemOptionDto optionDto) {
//        logHeaders(request, "AdminApiController addMenuItemOption");
        return serveCreated(() -> dataEngine.addOption(menuItemId, optionDto));
    }

    @Override
    public ResponseEntity<CreatedResponse> addMenuItem(final MenuItemDto menuItemDto) {
        return serveCreated(() -> dataEngine.addMenuItemFromDto(menuItemDto));
    }

    @Override
    public ResponseEntity<CreatedResponse> addNewMenuItemOption(@Valid final MenuItemOptionDto menuItemOptionDto) {
        return serveCreated(() -> dataEngine.createNewOption(menuItemOptionDto));
    }

    @Override
    public ResponseEntity<Void> deleteOption(final Integer optionId) {
//        logHeaders(request, "AdminApiController deleteOption");
        return serveOK(() -> dataEngine.deleteById(optionId));
    }

    @Override
    public ResponseEntity<Void> addAdmin(@Valid final UserDto userDto) {
        return ResponseUtility.serveOK(() -> userEngine.createUser(userDto, Role.ADMIN));
    }
}
