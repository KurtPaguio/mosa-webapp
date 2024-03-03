package com.example.mosawebapp.account.dto;

import com.example.mosawebapp.account.domain.Account;
import com.example.mosawebapp.account.domain.UserRole;
import java.util.ArrayList;
import java.util.List;

public class AccountDto {
  private String id;
  private String dateCreated;
  private String username;
  private String fullName;
  private String email;
  private String contactNumber;
  private String address;
  private UserRole userRole;

  public AccountDto(){}
  public AccountDto(String id, String dateCreated, String username, String fullName, String email, String contactNumber, String address,
      UserRole userRole) {
    this.id = id;
    this.dateCreated = dateCreated;
    this.username = username;
    this.fullName = fullName;
    this.email = email;
    this.contactNumber = contactNumber;
    this.address = address;
    this.userRole = userRole;
  }

  public AccountDto(String username, String fullName, String email, String contactNumber, UserRole userRole) {
    this.username = username;
    this.fullName = fullName;
    this.email = email;
    this.contactNumber = contactNumber;
    this.userRole = userRole;
  }

  public static AccountDto buildFromEntity(Account account){
    return new AccountDto(account.getId(), account.getDateCreated(), account.getUsername(), account.getFullName(), account.getEmail(),
        account.getContactNumber(), account.getAddress(), account.getUserRole());
  }

  public static List<AccountDto> buildFromEntities(List<Account> accounts){
    List<AccountDto> dtos = new ArrayList<>();

    for(Account account: accounts){
      dtos.add(buildFromEntity(account));
    }

    return dtos;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getDateCreated() {
    return dateCreated;
  }

  public void setDateCreated(String dateCreated) {
    this.dateCreated = dateCreated;
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
}
