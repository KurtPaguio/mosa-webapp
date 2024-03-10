package com.example.mosawebapp.mail;

import com.example.mosawebapp.account.domain.Account;
import com.example.mosawebapp.account.dto.AccountForm;
import com.example.mosawebapp.product.brand.domain.Brand;
import com.example.mosawebapp.product.brand.dto.BrandDto;
import com.example.mosawebapp.product.threadtype.domain.ThreadType;
import com.example.mosawebapp.product.threadtype.dto.ThreadTypeDto;
import com.example.mosawebapp.product.threadtypedetails.domain.ThreadTypeDetails;
import com.example.mosawebapp.product.threadtypedetails.dto.ThreadTypeDetailsDto;
import com.example.mosawebapp.utils.DateTimeFormatter;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {
  private static final String ADDED = "Added";
  private static final String UPDATED = "Updated";
  private static final String DELETED = "Deleted";
  @Autowired
  private JavaMailSender javaMailSender;
  @Value("${spring.mail.username}")
  private String fromEmail;

  private final SimpleMailMessage simpleMailMessage = new SimpleMailMessage();

  public void sendEmail(String mail, MailStructure mailStructure){
    simpleMailMessage.setFrom(fromEmail);
    simpleMailMessage.setSubject(mailStructure.getSubject());
    simpleMailMessage.setText(mailStructure.getMessage());
    simpleMailMessage.setTo(mail);

    javaMailSender.send(simpleMailMessage);
  }

  public void sendEmailForAccountCreation(AccountForm form){
    simpleMailMessage.setFrom(fromEmail);
    simpleMailMessage.setSubject("NO REPLY: Mosa Tire Supply Account Creation");
    simpleMailMessage.setText("Good Day, " + form.getFullName().toUpperCase() + "!" +
        "\n\nAn account associated with you has been created in Mosa Tire Supply Website. If you did not request this account creation, inform us "
        + "immediately through this email, mosatiresupply@gmail.com. Otherwise, here are your account credentials."
        + "\n\nEmail: " + form.getEmail()
        + "\nPassword: " + form.getPassword()
        + "\n\nOnce you received this email, login and change your password immediately to avoid data breach. Keep this email confidential. Thank you!"
    );
    simpleMailMessage.setTo(form.getEmail());

    javaMailSender.send(simpleMailMessage);
  }

  public void sendEmailForAccountRegistration(Account account){
    simpleMailMessage.setFrom(fromEmail);
    simpleMailMessage.setSubject("NO REPLY: Mosa Tire Supply Account Creation");
    simpleMailMessage.setText("Warm Welcome, " + account.getFullName().toUpperCase() + "!" +
        "\n\nYou've successfully created your account in Mosa Tire Supply. If you did not request this account creation, inform us "
        + "immediately through this email, mosatiresupply@gmail.com."
    );
    simpleMailMessage.setTo(account.getEmail());

    javaMailSender.send(simpleMailMessage);
  }
  public void sendEmailForLogin(String mail, long otp){
    simpleMailMessage.setFrom(fromEmail);
    simpleMailMessage.setSubject("NO REPLY: Mosa Tire Supply Login OTP");
    simpleMailMessage.setText("We've received a notification of your login attempt in Mosa Tire Supply. "
            + "If it's not you who requested it, please ignore this message. "
            + "\n\nHere's your login OTP: " + otp
    );
    simpleMailMessage.setTo(mail);

    javaMailSender.send(simpleMailMessage);
  }

  public void sendEmailForRegistration(String mail, long otp){
    simpleMailMessage.setFrom(fromEmail);
    simpleMailMessage.setSubject("NO REPLY: Mosa Tire Supply Registration OTP");
    simpleMailMessage.setText("We've received a notification of your account registration in Mosa Tire Supply. "
        + "If it's not you who requested it, please ignore this message."
        + "\n\nHere's your registration OTP: " + otp
    );
    simpleMailMessage.setTo(mail);

    javaMailSender.send(simpleMailMessage);
  }

  public void sendEmailForChangePassword(String mail, long otp){
    simpleMailMessage.setFrom(fromEmail);
    simpleMailMessage.setSubject("NO REPLY: Mosa Tire Supply Change Password OTP");
    simpleMailMessage.setText("We've received a notification of your password reset attempt in Mosa Tire Supply. "
            + "If it's not you who requested it, please ignore this message."
            + "\n\nHere's your reset password OTP: " + otp
    );
    simpleMailMessage.setTo(mail);

    javaMailSender.send(simpleMailMessage);
  }

  public void sendEmailForBrand(String mail, Brand brand, String action){
    String word = "";
    if (action.equalsIgnoreCase(ADDED)){
      word = "new brand added";
    }

    if(action.equalsIgnoreCase(UPDATED)){
      word = "brand updated";
    }

    if(action.equalsIgnoreCase(DELETED)){
      word = "brand deleted";
    }

    simpleMailMessage.setFrom(fromEmail);
    simpleMailMessage.setSubject("NO REPLY: Mosa Tire Supply New Brand");
    simpleMailMessage.setText("We've received a notification of a " + word + " at Mosa Tire Supply. "
        + "\nIf this is just a mistake, please make a necessary action to solve the problem. Otherwise, here are the details:"
        + "\n\nBrand Name: " + brand.getName()
        + "\nDate " + action + ": " + new Date()
    );

    simpleMailMessage.setTo(mail);
    javaMailSender.send(simpleMailMessage);
  }


  public void sendEmailForThreadType(String mail, Brand brand, ThreadType type, String action){
    String word = "";
    if (action.equalsIgnoreCase(ADDED)){
      word = "new thread type added";
    }

    if(action.equalsIgnoreCase(UPDATED)){
      word = "thread type updated";
    }

    if(action.equalsIgnoreCase(DELETED)){
      word = "thread type deleted";
    }

    simpleMailMessage.setFrom(fromEmail);
    simpleMailMessage.setSubject("NO REPLY: Mosa Tire Supply New Thread Type");
    simpleMailMessage.setText("We've received a notification of a " + word + " at Mosa Tire Supply. "
        + "\nIf this is just a mistake, please make a necessary action to solve the problem. Otherwise, here are the details:"
        + "\n\nBrand: " + brand.getName()
        + "\nThread Type: " + type.getType()
        + "\nDate " + action + ": " + new Date()
    );

    simpleMailMessage.setTo(mail);
    javaMailSender.send(simpleMailMessage);
  }

  public void sendEmailForThreadTypeDetails(String mail, ThreadTypeDetails details, String action){
    String word = "";
    if (action.equalsIgnoreCase(ADDED)){
      word = "new thread type details added";
    }

    if(action.equalsIgnoreCase(UPDATED)){
      word = "thread type details updated";
    }

    if(action.equalsIgnoreCase(DELETED)){
      word = "thread type details deleted";
    }

    simpleMailMessage.setFrom(fromEmail);
    simpleMailMessage.setSubject("NO REPLY: Mosa Tire Supply New Thread Type Details");
    simpleMailMessage.setText("We've received a notification of a " + word + " at Mosa Tire Supply. "
        + "\nIf this is just a mistake, please make a necessary action to solve the problem. Otherwise, here are the details:"
        + "\n\nThread Type: " + details.getThreadType().getType()
        + "\nWidth/Aspect Ratio/Diameter: " + details.getWidth() + "/" + details.getAspectRatio() + "/" + details.getDiameter()
        + "\nPrice: " + details.getPrice()
        + "\nDate " + action + ": " + new Date()
    );

    simpleMailMessage.setTo(mail);
    javaMailSender.send(simpleMailMessage);
  }
}
