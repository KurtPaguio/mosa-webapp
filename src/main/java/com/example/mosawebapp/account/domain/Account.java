package com.example.mosawebapp.account.domain;

import com.example.mosawebapp.validate.Validate;
import java.math.BigInteger;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.springframework.beans.factory.annotation.Value;

@Entity
public class Account {
  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  private String id;
  @Column
  private String username;
  @Column
  private String fullName;
  @Column
  private String email;
  @Column
  private String contactNumber;
  @Column
  private String address;
  @Column
  private String password;
  @Column
  @Enumerated(EnumType.STRING)
  private UserRole userRole;
  @Column
  private long loginOtp;
  @Column
  private long registerOtp;
  @Column
  private long changePasswordOtp;
  @OneToMany(cascade = CascadeType.REMOVE)
  @JoinTable(name = "accounts_roles", joinColumns = @JoinColumn(name = "account_id", referencedColumnName = "id"),
      inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
  @LazyCollection(LazyCollectionOption.FALSE)
  private List<Role> roles;

  @OneToOne(cascade = CascadeType.REMOVE)
  @JoinTable(name = "accounts_status", joinColumns = @JoinColumn(name = "account_id", referencedColumnName = "id"),
      inverseJoinColumns = @JoinColumn(name = "status_id", referencedColumnName = "id"))
  @LazyCollection(LazyCollectionOption.FALSE)
  private Status status;

  public Account(){}
  public Account(String username, String fullName, String email, String contactNumber, String address,
      String password, UserRole userRole) {
    this.username = username;
    this.fullName = fullName;
    this.email = email;
    this.contactNumber = contactNumber;
    this.address = address;
    this.password = password;
    this.userRole = userRole;
  }

  public Account(String username, String fullName, long loginOtp, long changePasswordOtp, long registerOtp) {
    this.username = username;
    this.fullName = fullName;

    if(loginOtp != 0)
      this.loginOtp = loginOtp;

    if(changePasswordOtp != 0)
      this.changePasswordOtp = changePasswordOtp;

    if(registerOtp != 0)
      this.registerOtp = registerOtp;
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

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public void setUserRole(UserRole userRole) {
    this.userRole = userRole;
  }

  public UserRole getUserRole() {
    return userRole;
  }

  public long getLoginOtp() {
    return loginOtp;
  }

  public void setLoginOtp(long loginOtp) {
    this.loginOtp = loginOtp;
  }

  public long getChangePasswordOtp() {
    return changePasswordOtp;
  }

  public void setChangePasswordOtp(long changePasswordOtp) {
    this.changePasswordOtp = changePasswordOtp;
  }

  public long getRegisterOtp() {
    return registerOtp;
  }

  public void setRegisterOtp(long registerOtp) {
    this.registerOtp = registerOtp;
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public List<Role> getRoles() {
    return roles;
  }

  public void setRoles(List<Role> roles) {
    this.roles = roles;
  }
}
