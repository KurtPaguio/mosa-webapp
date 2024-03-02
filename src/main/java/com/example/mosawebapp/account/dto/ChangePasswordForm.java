package com.example.mosawebapp.account.dto;

public class ChangePasswordForm {
  private String oldPassword;
  private String newPassword;
  private String finalPassword;

  public ChangePasswordForm(){}
  public ChangePasswordForm(String oldPassword, String newPassword, String finalPassword) {
    this.oldPassword = oldPassword;
    this.newPassword = newPassword;
    this.finalPassword = finalPassword;
  }

  public String getOldPassword() {
    return oldPassword;
  }

  public void setOldPassword(String oldPassword) {
    this.oldPassword = oldPassword;
  }

  public String getNewPassword() {
    return newPassword;
  }

  public void setNewPassword(String newPassword) {
    this.newPassword = newPassword;
  }

  public String getFinalPassword() {
    return finalPassword;
  }

  public void setFinalPassword(String finalPassword) {
    this.finalPassword = finalPassword;
  }
}
