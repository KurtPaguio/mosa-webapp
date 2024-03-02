package com.example.mosawebapp.account.dto;

import com.example.mosawebapp.account.domain.UserRole;

public class AccountForm {
  private String username;
  private String fullName;
  private String email;
  private String contactNumber;
  private String address;
  private String password;
  private UserRole userRole;

  public AccountForm(){}
  public AccountForm(String username, String fullName, String email, String contactNumber, String address,
      String password, UserRole userRole) {
    this.username = username;
    this.fullName = fullName;
    this.email = email;
    this.contactNumber = contactNumber;
    this.address = address;
    this.password = password;
    this.userRole = userRole;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getFullName() {
    return fullName;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getContactNumber() {
    return contactNumber;
  }

  public void setContactNumber(String contactNumber) {
    this.contactNumber = contactNumber;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public UserRole getUserRole() {
    return userRole;
  }

  public void setUserRole(UserRole userRole) {
    this.userRole = userRole;
  }
}
