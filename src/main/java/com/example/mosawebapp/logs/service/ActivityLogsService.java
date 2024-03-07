package com.example.mosawebapp.logs.service;

import com.example.mosawebapp.account.domain.Account;
import com.example.mosawebapp.account.domain.AccountRepository;
import com.example.mosawebapp.account.domain.UserRole;
import com.example.mosawebapp.account.service.AccountService;
import com.example.mosawebapp.exceptions.NotFoundException;
import com.example.mosawebapp.exceptions.ValidationException;
import com.example.mosawebapp.logs.domain.ActivityLogs;
import com.example.mosawebapp.logs.domain.ActivityLogsRepository;
import com.example.mosawebapp.product.domain.Product;
import com.example.mosawebapp.scheduling.domain.Schedule;
import com.example.mosawebapp.security.JwtGenerator;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ActivityLogsService {
  private static final Logger logger = LoggerFactory.getLogger(ActivityLogsService.class);
  private final ActivityLogsRepository activityLogsRepository;
  private final AccountRepository accountRepository;
  private final JwtGenerator jwtGenerator;
  public ActivityLogsService(ActivityLogsRepository activityLogsRepository,
      AccountRepository accountRepository, JwtGenerator jwtGenerator) {
    this.activityLogsRepository = activityLogsRepository;
    this.accountRepository = accountRepository;
    this.jwtGenerator = jwtGenerator;
  }

  public List<ActivityLogs> getAllLogs(String token){
    validateIfAccountIsAdmin(token);

    return activityLogsRepository.findAll();
  }
  public void loginActivity(Account account){
    String actor = account.getFullName();
    String message = actor + " just logged into the system";
    boolean isStaff = account.getUserRole() != UserRole.CUSTOMER;

    ActivityLogs log = new ActivityLogs(new Date(), actor, message, isStaff);
    activityLogsRepository.save(log);
  }

  public void logoutActivity(Account account){
    String actor = account.getFullName();
    String message = actor + " just logged out of the system";
    boolean isStaff = account.getUserRole() != UserRole.CUSTOMER;

    ActivityLogs log = new ActivityLogs(new Date(), actor, message, isStaff);
    activityLogsRepository.save(log);
  }

  public void adminCreateAccountActivity(Account user, Account createdAccount){
    String actor = user.getFullName();
    String message = user.getFullName() + " just created an account for " + createdAccount.getFullName();

    ActivityLogs log = new ActivityLogs(new Date(), actor, message, true);
    activityLogsRepository.save(log);
  }

  public void adminUpdateAccountActivity(Account user, Account updatedAccount) {
    String actor = user.getFullName();
    String message =
        user.getFullName() + " just updated the account of " + updatedAccount.getFullName();

    ActivityLogs log = new ActivityLogs(new Date(), actor, message, true);
    activityLogsRepository.save(log);
  }

  public void adminDeleteAccountActivity(Account user, Account deletedAccount) {
    String actor = user.getFullName();
    String message =
        user.getFullName() + " just deleted the account of " + deletedAccount.getFullName();

    ActivityLogs log = new ActivityLogs(new Date(), actor, message, true);
    activityLogsRepository.save(log);
  }

  public void userUpdateAccountActivity(Account account){
    String actor = account.getFullName();
    String message =
        account.getFullName() + " just updated their account";

    ActivityLogs log = new ActivityLogs(new Date(), actor, message, false);
    activityLogsRepository.save(log);
  }

  public void userDeleteAccountActivity(Account account){
    String actor = account.getFullName();
    String message =
        account.getFullName() + " just deleted their account";

    ActivityLogs log = new ActivityLogs(new Date(), actor, message, false);
    activityLogsRepository.save(log);
  }

  public void changePasswordActivity(Account account){
    String actor = account.getFullName();
    String message = "Account Password of " + actor + " has been changed";
    boolean isStaff = account.getUserRole() != UserRole.CUSTOMER;

    ActivityLogs log = new ActivityLogs(new Date(), actor, message, isStaff);
    activityLogsRepository.save(log);
  }

  public void adminChangePasswordActivity(Account user, Account targetUser){
    String actor = user.getFullName();
    String message = actor + " changed the password of " + targetUser.getFullName();

    ActivityLogs log = new ActivityLogs(new Date(), actor, message, true);
    activityLogsRepository.save(log);
  }

  public void addProductActivity(Account account, Product product){
    String actor = account.getFullName();
    String message = actor + " added a product: " + product.getName();

    ActivityLogs log = new ActivityLogs(new Date(), actor, message, true);
    activityLogsRepository.save(log);
  }

  public void updateProductActivity(Account account, Product product){
    String actor = account.getFullName();
    String message = actor + " updated the product: " + product.getName();

    ActivityLogs log = new ActivityLogs(new Date(), actor, message, true);
    activityLogsRepository.save(log);
  }

  public void deleteProductActivity(Account account, Product product){
    String actor = account.getFullName();
    String message = actor + " deleted the product: " + product.getName();

    ActivityLogs log = new ActivityLogs(new Date(), actor, message, true);
    activityLogsRepository.save(log);
  }

  public void makeSchedule(Account account, Schedule schedule){
    String actor = account.getFullName();
    String message = actor + " just submitted a schedule for service on " + schedule.getDateScheduled();

    ActivityLogs log = new ActivityLogs(new Date(), actor, message, false);
    activityLogsRepository.save(log);
  }

  public void approveSchedule(Account account, Schedule schedule){
    String actor = account.getFullName();
    String message = actor + " just approved the schedule for service on " + schedule.getDateScheduled() +
        " submitted by " + schedule.getOrderedBy().getFullName();

    ActivityLogs log = new ActivityLogs(new Date(), actor, message, true);
    activityLogsRepository.save(log);
  }

  public void validateIfAccountIsAdmin(String token){
    String accId = jwtGenerator.getUserFromJWT(token);
    Account adminAccount = accountRepository.findById(accId).orElseThrow(() -> new NotFoundException("Account does not exists"));

    if(!adminAccount.getUserRole().equals(UserRole.ADMINISTRATOR)){
      throw new ValidationException("Only Administrators have access to this feature");
    }
  }
}
