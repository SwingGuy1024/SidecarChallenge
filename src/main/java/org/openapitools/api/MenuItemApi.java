/**
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech) (unset).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
package org.openapitools.api;

import org.openapitools.model.MenuItemDto;
import io.swagger.annotations.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2021-01-09T10:47:45.444934-08:00[America/Los_Angeles]")
@Validated
@Api(value = "menuItem", description = "the menuItem API")
public interface MenuItemApi {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * GET /menuItem : Get all menu items.
     * Retrieve all menu items 
     *
     * @return Found (status code 200)
     */
    @ApiOperation(value = "Get all menu items.", nickname = "getAll", notes = "Retrieve all menu items ", response = MenuItemDto.class, responseContainer = "List", tags={  })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Found", response = MenuItemDto.class, responseContainer = "List") })
    @GetMapping(
        value = "/menuItem",
        produces = { "application/json" }
    )
    default ResponseEntity<List<MenuItemDto>> getAll() {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"expired\" : true, \"allowedOptions\" : [ { \"deltaPrice\" : 5.962133916683182, \"name\" : \"name\", \"id\" : 5 }, { \"deltaPrice\" : 5.962133916683182, \"name\" : \"name\", \"id\" : 5 } ], \"name\" : \"name\", \"itemPrice\" : 6.027456183070403, \"id\" : 0, \"parentId\" : 1 }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }


    /**
     * GET /menuItem/{id} : Gets a menuItem by ID
     * Gets a MenuItem by its ID.
     *
     * @param id ID of menuItem to find (required)
     * @return Found (status code 200)
     *         or NotFound (status code 404)
     */
    @ApiOperation(value = "Gets a menuItem by ID", nickname = "getMenuItem", notes = "Gets a MenuItem by its ID.", response = MenuItemDto.class, tags={  })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Found", response = MenuItemDto.class),
        @ApiResponse(code = 404, message = "NotFound") })
    @GetMapping(
        value = "/menuItem/{id}",
        produces = { "application/json" }
    )
    default ResponseEntity<MenuItemDto> getMenuItem(@ApiParam(value = "ID of menuItem to find",required=true) @PathVariable("id") Integer id) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"expired\" : true, \"allowedOptions\" : [ { \"deltaPrice\" : 5.962133916683182, \"name\" : \"name\", \"id\" : 5 }, { \"deltaPrice\" : 5.962133916683182, \"name\" : \"name\", \"id\" : 5 } ], \"name\" : \"name\", \"itemPrice\" : 6.027456183070403, \"id\" : 0, \"parentId\" : 1 }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

}
