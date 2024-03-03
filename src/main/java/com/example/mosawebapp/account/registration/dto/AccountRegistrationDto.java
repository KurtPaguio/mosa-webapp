package com.example.mosawebapp.account.registration.dto;

import com.example.mosawebapp.account.domain.AccountStatus;
import com.example.mosawebapp.account.domain.UserRole;
import com.example.mosawebapp.account.registration.domain.AccountRegistration;
import java.util.ArrayList;
import java.util.List;

public class AccountRegistrationDto {
  private String id;
  private String username;
  private String fullName;
  private String email;
  private String contactNumber;
  private String address;
  private UserRole userRole;
  private AccountStatus status;

  public AccountRegistrationDto(){}
  public AccountRegistrationDto(String id, String username, String fullName, String email,
      String contactNumber, String address, UserRole userRole, AccountStatus status) {
    this.id = id;
    this.username = username;
    this.fullName = fullName;
    this.email = email;
    this.contactNumber = contactNumber;
    this.address = address;
    this.userRole = userRole;
    this.status = status;
  }

  public static AccountRegistrationDto buildFromEntity(AccountRegistration register){
    return new AccountRegistrationDto(register.getId(), register.getUsername(), register.getFullName(), register.getEmail(),
        register.getContactNumber(), register.getAddress(), register.getUserRole(),
        register.getStatus());
  }

  public static List<AccountRegistrationDto> buildFromEntities(List<AccountRegistration> registers){
    List<AccountRegistrationDto> dtos = new ArrayList<>();

    for(AccountRegistration register: registers){
      dtos.add(buildFromEntity(register));
    }

    return dtos;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
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

  public UserRole getUserRole() {
    return userRole;
  }

  public void setUserRole(UserRole userRole) {
    this.userRole = userRole;
  }

  public AccountStatus getStatus() {
    return status;
  }

  public void setStatus(AccountStatus status) {
    this.status = status;
  }
}
