package org.openapitools.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import org.openapitools.jackson.nullable.JsonNullable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * Option for a MenuItem
 */
@ApiModel(description = "Option for a MenuItem")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2021-01-17T18:11:48.695722-08:00[America/Los_Angeles]")
public class MenuItemOptionDto   {
  @JsonProperty("name")
  private String name;

  @JsonProperty("deltaPrice")
  private BigDecimal deltaPrice;

  @JsonProperty("id")
  private Integer id;

  public MenuItemOptionDto name(String name) {
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

  public MenuItemOptionDto deltaPrice(BigDecimal deltaPrice) {
    this.deltaPrice = deltaPrice;
    return this;
  }

  /**
   * Floating point price. Strings are easier to work with.
   * @return deltaPrice
  */
  @ApiModelProperty(value = "Floating point price. Strings are easier to work with.")

  @Valid

  public BigDecimal getDeltaPrice() {
    return deltaPrice;
  }

  public void setDeltaPrice(BigDecimal deltaPrice) {
    this.deltaPrice = deltaPrice;
  }

  public MenuItemOptionDto id(Integer id) {
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


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MenuItemOptionDto menuItemOptionDto = (MenuItemOptionDto) o;
    return Objects.equals(this.name, menuItemOptionDto.name) &&
        Objects.equals(this.deltaPrice, menuItemOptionDto.deltaPrice) &&
        Objects.equals(this.id, menuItemOptionDto.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, deltaPrice, id);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MenuItemOptionDto {\n");
    
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    deltaPrice: ").append(toIndentedString(deltaPrice)).append("\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
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

