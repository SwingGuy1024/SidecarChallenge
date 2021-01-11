package org.openapitools.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.validation.Valid;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

/**
 * OrderNodeDto
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2021-01-09T10:47:45.444934-08:00[America/Los_Angeles]")
public class OrderNodeDto   {
  @JsonProperty("id")
  private Integer id;

  @JsonProperty("orderId")
  private Integer orderId;

  @JsonProperty("parentTree")
  private OrderNodeDto parentTree;

  @JsonProperty("menuItemId")
  private Integer menuItemId;

  @JsonProperty("options")
  @Valid
  private List<OrderNodeDto> options = null;

  public OrderNodeDto id(Integer id) {
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

  public OrderNodeDto orderId(Integer orderId) {
    this.orderId = orderId;
    return this;
  }

  /**
   * Get orderId
   * @return orderId
  */
  @ApiModelProperty(value = "")


  public Integer getOrderId() {
    return orderId;
  }

  public void setOrderId(Integer orderId) {
    this.orderId = orderId;
  }

  public OrderNodeDto parentTree(OrderNodeDto parentTree) {
    this.parentTree = parentTree;
    return this;
  }

  /**
   * Get parentTree
   * @return parentTree
  */
  @ApiModelProperty(value = "")

  @Valid

  public OrderNodeDto getParentTree() {
    return parentTree;
  }

  public void setParentTree(OrderNodeDto parentTree) {
    this.parentTree = parentTree;
  }

  public OrderNodeDto menuItemId(Integer menuItemId) {
    this.menuItemId = menuItemId;
    return this;
  }

  /**
   * Get menuItemId
   * @return menuItemId
  */
  @ApiModelProperty(value = "")


  public Integer getMenuItemId() {
    return menuItemId;
  }

  public void setMenuItemId(Integer menuItemId) {
    this.menuItemId = menuItemId;
  }

  public OrderNodeDto options(List<OrderNodeDto> options) {
    this.options = options;
    return this;
  }

  public OrderNodeDto addOptionsItem(OrderNodeDto optionsItem) {
    if (this.options == null) {
      this.options = new ArrayList<>();
    }
    this.options.add(optionsItem);
    return this;
  }

  /**
   * Get options
   * @return options
  */
  @ApiModelProperty(value = "")

  @Valid

  public List<OrderNodeDto> getOptions() {
    return options;
  }

  public void setOptions(List<OrderNodeDto> options) {
    this.options = options;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OrderNodeDto orderNodeDto = (OrderNodeDto) o;
    return Objects.equals(this.id, orderNodeDto.id) &&
        Objects.equals(this.orderId, orderNodeDto.orderId) &&
        Objects.equals(this.parentTree, orderNodeDto.parentTree) &&
        Objects.equals(this.menuItemId, orderNodeDto.menuItemId) &&
        Objects.equals(this.options, orderNodeDto.options);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, orderId, parentTree, menuItemId, options);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OrderNodeDto {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    orderId: ").append(toIndentedString(orderId)).append("\n");
    sb.append("    parentTree: ").append(toIndentedString(parentTree)).append("\n");
    sb.append("    menuItemId: ").append(toIndentedString(menuItemId)).append("\n");
    sb.append("    options: ").append(toIndentedString(options)).append("\n");
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

