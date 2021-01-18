package org.openapitools.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.openapitools.model.MenuItemOptionDto;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * MenuItemDto
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2021-01-17T18:11:48.695722-08:00[America/Los_Angeles]")
public class MenuItemDto   {
  @JsonProperty("id")
  private Integer id;

  @JsonProperty("name")
  private String name;

  @JsonProperty("itemPrice")
  private BigDecimal itemPrice;

  @JsonProperty("parentId")
  private Integer parentId;

  @JsonProperty("expired")
  private Boolean expired;

  @JsonProperty("allowedOptions")
  @Valid
  private List<MenuItemOptionDto> allowedOptions = null;

  public MenuItemDto id(Integer id) {
    this.id = id;
    return this;
  }

  /**
   * Get id
   * @return id
  */
  @ApiModelProperty(value = "")


  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public MenuItemDto name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Get name
   * @return name
  */
  @ApiModelProperty(value = "")


  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public MenuItemDto itemPrice(BigDecimal itemPrice) {
    this.itemPrice = itemPrice;
    return this;
  }

  /**
   * Price. (BigDecimal)
   * @return itemPrice
  */
  @ApiModelProperty(value = "Price. (BigDecimal)")

  @Valid

  public BigDecimal getItemPrice() {
    return itemPrice;
  }

  public void setItemPrice(BigDecimal itemPrice) {
    this.itemPrice = itemPrice;
  }

  public MenuItemDto parentId(Integer parentId) {
    this.parentId = parentId;
    return this;
  }

  /**
   * Get parentId
   * @return parentId
  */
  @ApiModelProperty(value = "")


  public Integer getParentId() {
    return parentId;
  }

  public void setParentId(Integer parentId) {
    this.parentId = parentId;
  }

  public MenuItemDto expired(Boolean expired) {
    this.expired = expired;
    return this;
  }

  /**
   * Get expired
   * @return expired
  */
  @ApiModelProperty(value = "")


  public Boolean getExpired() {
    return expired;
  }

  public void setExpired(Boolean expired) {
    this.expired = expired;
  }

  public MenuItemDto allowedOptions(List<MenuItemOptionDto> allowedOptions) {
    this.allowedOptions = allowedOptions;
    return this;
  }

  public MenuItemDto addAllowedOptionsItem(MenuItemOptionDto allowedOptionsItem) {
    if (this.allowedOptions == null) {
      this.allowedOptions = new ArrayList<>();
    }
    this.allowedOptions.add(allowedOptionsItem);
    return this;
  }

  /**
   * Get allowedOptions
   * @return allowedOptions
  */
  @ApiModelProperty(value = "")

  @Valid

  public List<MenuItemOptionDto> getAllowedOptions() {
    return allowedOptions;
  }

  public void setAllowedOptions(List<MenuItemOptionDto> allowedOptions) {
    this.allowedOptions = allowedOptions;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MenuItemDto menuItemDto = (MenuItemDto) o;
    return Objects.equals(this.id, menuItemDto.id) &&
        Objects.equals(this.name, menuItemDto.name) &&
        Objects.equals(this.itemPrice, menuItemDto.itemPrice) &&
        Objects.equals(this.parentId, menuItemDto.parentId) &&
        Objects.equals(this.expired, menuItemDto.expired) &&
        Objects.equals(this.allowedOptions, menuItemDto.allowedOptions);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, itemPrice, parentId, expired, allowedOptions);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MenuItemDto {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    itemPrice: ").append(toIndentedString(itemPrice)).append("\n");
    sb.append("    parentId: ").append(toIndentedString(parentId)).append("\n");
    sb.append("    expired: ").append(toIndentedString(expired)).append("\n");
    sb.append("    allowedOptions: ").append(toIndentedString(allowedOptions)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

