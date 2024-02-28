package com.example.mosawebapp.account.dto;

import com.example.mosawebapp.account.domain.Account;
import com.example.mosawebapp.account.domain.UserRole;
import java.util.ArrayList;
import java.util.List;

public class AccountDto {
  private String id;
  private String username;
  private String fullName;
  private String email;
  private String contactNumber;
  private String password;
  private UserRole userRole;

  public AccountDto(){}
  public AccountDto(String id, String username, String fullName, String email, String contactNumber,
      String password, UserRole userRole) {
    this.id = id;
    this.username = username;
    this.fullName = fullName;
    this.email = email;
    this.contactNumber = contactNumber;
    this.password = password;
    this.userRole = userRole;
  }

  public static AccountDto buildFromEntity(Account account){
    return new AccountDto(account.getId(), account.getUsername(), account.getFullName(), account.getEmail(),
        account.getContactNumber(), account.getPassword(), account.getRole());
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

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public UserRole getRole() {
    return userRole;
  }

  public void setRole(UserRole userRole) {
    this.userRole = userRole;
  }
}
