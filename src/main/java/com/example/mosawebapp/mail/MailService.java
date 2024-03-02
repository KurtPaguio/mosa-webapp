package com.example.mosawebapp.mail;

import com.example.mosawebapp.account.domain.Account;
import com.example.mosawebapp.account.dto.AccountForm;
import jakarta.mail.internet.MimeMessage;
import java.math.BigInteger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

@Service
public class MailService {
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
    simpleMailMessage.setSubject("Mosa Tire Supply Account Creation");
    simpleMailMessage.setText("Good Day, " + form.getFullName().toUpperCase() + "!" +
        "\n\nAn account associated with you has been created in Mosa Tire Supply Website. If you did not request this account creation, inform us "
        + "immediately through this email. Otherwise, here are your account credentials."
        + "\n\nEmail: " + form.getEmail()
        + "\nUsername: " + form.getUsername()
        + "\nPassword: " + form.getPassword()
        + "\n\nOnce you received this email, login and change your password immediately to avoid data breach. Keep this email confidential. Thank you!"
    );
    simpleMailMessage.setTo(form.getEmail());

    javaMailSender.send(simpleMailMessage);
  }

  public void sendEmailForAccountRegistration(Account account){
    simpleMailMessage.setFrom(fromEmail);
    simpleMailMessage.setSubject("Mosa Tire Supply Account Creation");
    simpleMailMessage.setText("Warm Welcome, " + account.getFullName().toUpperCase() + "!" +
        "\n\nYou've successfully created your account in Mosa Tire Supply. If you did not request this account creation, inform us "
        + "immediately through this email."
    );
    simpleMailMessage.setTo(account.getEmail());

    javaMailSender.send(simpleMailMessage);
  }
  public void sendEmailForLogin(String mail, long otp){
    simpleMailMessage.setFrom(fromEmail);
    simpleMailMessage.setSubject("Mosa Tire Supply Login OTP");
    simpleMailMessage.setText("We've received a notification of your login attempt in Mosa Tire Supply. "
            + "If it's not you who requested it, please ignore this message. "
            + "\n\nHere's your login OTP: " + otp
    );
    simpleMailMessage.setTo(mail);

    javaMailSender.send(simpleMailMessage);
  }

  public void sendEmailForRegistration(String mail, long otp){
    simpleMailMessage.setFrom(fromEmail);
    simpleMailMessage.setSubject("Mosa Tire Supply Registration OTP");
    simpleMailMessage.setText("We've received a notification of your account registration in Mosa Tire Supply. "
        + "If it's not you who requested it, please ignore this message."
        + "\n\nHere's your registration OTP: " + otp
    );
    simpleMailMessage.setTo(mail);

    javaMailSender.send(simpleMailMessage);
  }

  public void sendEmailForChangePassword(String mail, long otp){
    simpleMailMessage.setFrom(fromEmail);
    simpleMailMessage.setSubject("Mosa Tire Supply Change Password OTP");
    simpleMailMessage.setText("We've received a notification of your password reset attempt in Mosa Tire Supply. "
            + "If it's not you who requested it, please ignore this message."
            + "\n\nHere's your reset password OTP: " + otp
    );
    simpleMailMessage.setTo(mail);

    javaMailSender.send(simpleMailMessage);
  }
}
