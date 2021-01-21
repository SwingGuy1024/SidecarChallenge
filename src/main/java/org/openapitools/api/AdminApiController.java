package org.openapitools.api;

import java.util.Optional;
import javax.validation.Valid;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openapitools.entity.MenuItem;
import org.openapitools.entity.MenuItemOption;
import org.openapitools.framework.PojoUtility;
import org.openapitools.framework.exception.BadRequest400Exception;
import org.openapitools.model.CreatedResponse;
import org.openapitools.model.MenuItemDto;
import org.openapitools.model.MenuItemOptionDto;
import org.openapitools.repositories.MenuItemOptionRepository;
import org.openapitools.repositories.MenuItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.NativeWebRequest;

import static org.openapitools.framework.PojoUtility.*;
import static org.openapitools.framework.ResponseUtility.*;

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2021-01-08T23:22:44.934923-08:00[America/Los_Angeles]")
@Controller
@RequestMapping("${openapi.customerOrders.base-path:}")
public class AdminApiController implements AdminApi {

    private static final Logger log = LoggerFactory.getLogger(AdminApiController.class);

    private final NativeWebRequest request;

    private final MenuItemRepository menuItemRepository;

    private final MenuItemOptionRepository menuItemOptionRepository;
    
    private final ObjectMapper objectMapper;

    @Autowired
    public AdminApiController(
        final NativeWebRequest request,
        final ObjectMapper objectMapper,
        final MenuItemRepository menuItemRepository,
        final MenuItemOptionRepository menuItemOptionRepository
    ) {
        this.request = request;
        this.objectMapper = objectMapper;
        this.menuItemRepository = menuItemRepository;
        this.menuItemOptionRepository = menuItemOptionRepository;
        log.debug("instantiating AdminApiController");
    }

    // I would never wrap this in an Optional, but the interface is generated.
    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);
    }

    @Override
    public ResponseEntity<CreatedResponse> addMenuItemOption(final Integer menuItemId, final MenuItemOptionDto optionDto) {
//        logHeaders(request, "AdminApiController addMenuItemOption");
        return serveCreated(() -> {
            return addOption(menuItemId, optionDto);
        });
    }

    private CreatedResponse addOption(final Integer menuItemId, final MenuItemOptionDto optionDto) {
        confirmNotEmpty(optionDto.getName()); // throws ResponseException
        MenuItemOption menuItemOption = objectMapper.convertValue(optionDto, MenuItemOption.class);
        final MenuItem menuItem = menuItemRepository.getOne(menuItemId);
        menuItemOption.setMenuItem(menuItem);
        MenuItemOption savedOption = menuItemOptionRepository.save(menuItemOption);
        return fromId(savedOption.getId());
    }

    @Override
    public ResponseEntity<CreatedResponse> addMenuItem(final MenuItemDto menuItemDto) {
        return serveCreated(() -> {
            return addMenuItemFromDto(menuItemDto);
        });
    }

    private CreatedResponse addMenuItemFromDto(final MenuItemDto menuItemDto) {
        for (MenuItemOptionDto option : skipNull(menuItemDto.getAllowedOptions())) {
            final String optionName = option.getName();
            if ((optionName == null) || optionName.isEmpty()) {
                throw new BadRequest400Exception("Missing Food Option name for item");
            }
        }
        MenuItem menuItem = convertMenuItem(menuItemDto);
        log.trace("MenuItem: {}", menuItem);
        MenuItem savedItem = menuItemRepository.save(menuItem);
        final Integer id = savedItem.getId();
        log.debug("added menuItem with id {}", id);
        return fromId(id);
    }

    @Override
    public ResponseEntity<CreatedResponse> addNewMenuItemOption(@Valid final MenuItemOptionDto menuItemOptionDto) {
        return serveCreated(() -> createNewOption(menuItemOptionDto));
    }

    private CreatedResponse createNewOption(final MenuItemOptionDto menuItemOptionDto) {
        MenuItemOption menuItemOption = convertMenuItemOption(menuItemOptionDto);
        log.trace("MenuItemOption: {}", menuItemOption);
        MenuItemOption savedItem = menuItemOptionRepository.save(menuItemOption);
        final Integer id = savedItem.getId();
        log.debug("MenuItemOption added with id {}", id);
        final CreatedResponse createdResponse = fromId(id);
        return createdResponse;
    }

    @Override
    public ResponseEntity<Void> deleteOption(final Integer optionId) {
//        logHeaders(request, "AdminApiController deleteOption");
        return serveOK(() -> {
            return deleteById(optionId);
        });
    }

    private Void deleteById(final Integer optionId) {
        log.debug("Deleting menuItemOption with id {}", optionId);

        
        MenuItemOption itemToDelete = menuItemOptionRepository.getOne(optionId);

        // Before I can successfully delete the menuItemOption, I first have to set its menuItem to null. If I don't
        // do that, the delete call will fail. It doesn't help to set Cascade to Remove in the @ManyToOne annotation in 
        // MenuItemOption. Since it's set to ALL in MenuItem's @OneToMany annotation, the Cascade value doesn't seem to 
        // affect this.
        itemToDelete.setMenuItem(null);
        menuItemOptionRepository.save(itemToDelete);

        menuItemOptionRepository.delete(itemToDelete);
        return null;
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
    
    private MenuItemOption convertMenuItemOption(final MenuItemOptionDto menuItemOptionDto) {
        return objectMapper.convertValue(menuItemOptionDto, MenuItemOption.class);
    }

}
