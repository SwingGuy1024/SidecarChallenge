package com.neptunedreams.entity;

import java.util.Collection;
import java.util.Collections;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import com.neptunedreams.userservice.Role;
import com.neptunedreams.model.UserAuthority;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 1/6/21
 * <p>Time: 11:57 AM
 *
 * @author Miguel Mu\u00f1oz
 */
@Entity
public class User implements UserDetails {
  @Id
  @Column(unique = true)
  private String username = null;

  private String password = null;

  @Column(unique = true)
  private String email = null;

  @Column(unique = true)
  private String mobilePhone = null;

  @Column(unique = true)
  private String landPhone = null;

  private Role role = null;

  @Override
  public String getUsername() {
    return username;
  }

  public void setUsername(final String username) {
    this.username = username;
  }

  @Override
  public String getPassword() {
    return password;
  }

  public void setPassword(final String password) {
    this.password = password;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(final String email) {
    this.email = email;
  }

  public String getMobilePhone() {
    return mobilePhone;
  }

  public void setMobilePhone(final String mobilePhone) {
    this.mobilePhone = mobilePhone;
  }

  public String getLandPhone() {
    return landPhone;
  }

  public void setLandPhone(final String landPhone) {
    this.landPhone = landPhone;
  }

  public Role getRole() {
    return role;
  }

  public void setRole(final Role role) {
    this.role = role;
  }

//  public Boolean getAccountNonExpired() {
//    return Boolean.TRUE; // isAccountNonExpired;
//  }

//  public void setAccountNonExpired(final Boolean accountNonExpired) {
//    isAccountNonExpired = accountNonExpired;
//  }

//  public Boolean getAccountNonLocked() {
//    return Boolean.TRUE; // isAccountNonLocked;
//  }

//  public void setAccountNonLocked(final Boolean accountNonLocked) {
//    isAccountNonLocked = accountNonLocked;
//  }

//  public Boolean getCredentialNonExpired() {
//    return Boolean.TRUE; // isCredentialNonExpired;
//  }

//  public void setCredentialNonExpired(final Boolean credentialNonExpired) {
//    isCredentialNonExpired = credentialNonExpired;
//  }

//  public Boolean getEnabled() {
//    return Boolean.TRUE; // isEnabled;
//  }

//  public void setEnabled(final Boolean enabled) {
//    isEnabled = enabled;
//  }

  // Okay, this is a quick and dirty implementation, because this is just a demo. I would never do this in production!
  @Override
  public boolean isAccountNonExpired() {
    return true; // isAccountNonExpired;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true; // isAccountNonLocked;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true; // isCredentialNonExpired;
  }

  @Override
  public boolean isEnabled() {
    return true; // isEnabled;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return Collections.singleton(new UserAuthority(getRole()));
  }
}
