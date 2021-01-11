package org.openapitools.model;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.validation.Valid;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModelProperty;

/**
 * CustomerOrderDto
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2021-01-09T10:47:45.444934-08:00[America/Los_Angeles]")
public class CustomerOrderDto   {
  @JsonProperty("menuItem")
  private MenuItemDto menuItem;

  @JsonProperty("options")
  @Valid
  private List<MenuItemDto> options = null;

  @JsonProperty("customer")
  private UserDto customer;

  /**
   * Order complete
   */
  public enum StatusEnum {
    PENDING("PENDING"),
    
    COMPLETE("COMPLETE"),
    
    CANCELLED("CANCELLED");

    private String value;

    StatusEnum(String value) {
      this.value = value;
    }

    @JsonValue
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static StatusEnum fromValue(String value) {
      for (StatusEnum b : StatusEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  @JsonProperty("status")
  private StatusEnum status;

  @JsonProperty("id")
  private Integer id;

  @JsonProperty("orderTime")
  @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime orderTime;

  @JsonProperty("completeTime")
  @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime completeTime;

  public CustomerOrderDto menuItem(MenuItemDto menuItem) {
    this.menuItem = menuItem;
    return this;
  }

  /**
   * Get menuItem
   * @return menuItem
  */
  @ApiModelProperty(value = "")

  @Valid

  public MenuItemDto getMenuItem() {
    return menuItem;
  }

  public void setMenuItem(MenuItemDto menuItem) {
    this.menuItem = menuItem;
  }

  public CustomerOrderDto options(List<MenuItemDto> options) {
    this.options = options;
    return this;
  }

  public CustomerOrderDto addOptionsItem(MenuItemDto optionsItem) {
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

  public List<MenuItemDto> getOptions() {
    return options;
  }

  public void setOptions(List<MenuItemDto> options) {
    this.options = options;
  }

  public CustomerOrderDto customer(UserDto customer) {
    this.customer = customer;
    return this;
  }

  /**
   * Get customer
   * @return customer
  */
  @ApiModelProperty(value = "")

  @Valid

  public UserDto getCustomer() {
    return customer;
  }

  public void setCustomer(UserDto customer) {
    this.customer = customer;
  }

  public CustomerOrderDto status(StatusEnum status) {
    this.status = status;
    return this;
  }

  /**
   * Order complete
   * @return status
  */
  @ApiModelProperty(value = "Order complete")


  public StatusEnum getStatus() {
    return status;
  }

  public void setStatus(StatusEnum status) {
    this.status = status;
  }

  public CustomerOrderDto id(Integer id) {
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

  public CustomerOrderDto orderTime(OffsetDateTime orderTime) {
    this.orderTime = orderTime;
    return this;
  }

  /**
   * Get orderTime
   * @return orderTime
  */
  @ApiModelProperty(value = "")

  @Valid

  public OffsetDateTime getOrderTime() {
    return orderTime;
  }

  public void setOrderTime(OffsetDateTime orderTime) {
    this.orderTime = orderTime;
  }

  public CustomerOrderDto completeTime(OffsetDateTime completeTime) {
    this.completeTime = completeTime;
    return this;
  }

  /**
   * Get completeTime
   * @return completeTime
  */
  @ApiModelProperty(value = "")

  @Valid

  public OffsetDateTime getCompleteTime() {
    return completeTime;
  }

  public void setCompleteTime(OffsetDateTime completeTime) {
    this.completeTime = completeTime;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CustomerOrderDto customerOrderDto = (CustomerOrderDto) o;
    return Objects.equals(this.menuItem, customerOrderDto.menuItem) &&
        Objects.equals(this.options, customerOrderDto.options) &&
        Objects.equals(this.customer, customerOrderDto.customer) &&
        Objects.equals(this.status, customerOrderDto.status) &&
        Objects.equals(this.id, customerOrderDto.id) &&
        Objects.equals(this.orderTime, customerOrderDto.orderTime) &&
        Objects.equals(this.completeTime, customerOrderDto.completeTime);
  }

  @Override
  public int hashCode() {
    return Objects.hash(menuItem, options, customer, status, id, orderTime, completeTime);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CustomerOrderDto {\n");
    
    sb.append("    menuItem: ").append(toIndentedString(menuItem)).append("\n");
    sb.append("    options: ").append(toIndentedString(options)).append("\n");
    sb.append("    customer: ").append(toIndentedString(customer)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    orderTime: ").append(toIndentedString(orderTime)).append("\n");
    sb.append("    completeTime: ").append(toIndentedString(completeTime)).append("\n");
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

