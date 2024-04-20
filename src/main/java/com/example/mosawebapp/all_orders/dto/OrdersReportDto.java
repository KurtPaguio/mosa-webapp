package com.example.mosawebapp.all_orders.dto;

import com.example.mosawebapp.cart.dto.CartDto;
import com.example.mosawebapp.exceptions.ValidationException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Stream;
import com.example.mosawebapp.kiosk.dto.KioskDto;
import com.example.mosawebapp.onsite_order.dto.OnsiteOrderDto;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrdersReportDto {
  private static final Logger logger = LoggerFactory.getLogger(OrdersReportDto.class);
  private static final String DATE_FORMAT = "MM-dd-yyyy";
  private double dailyOrders;
  private double weeklyOrders;
  private double monthlyOrders;
  private long currentDayCount;
  private long currentWeekCount;
  private long currentMonthCount;
  private String topBrandOrdered;
  private List<OrdersDto> orders;

  public OrdersReportDto() {
  }
  public OrdersReportDto(List<OrdersDto> orders) {
    this.topBrandOrdered=computeBrandCount(orders);
    this.dailyOrders=computeDailyOrders(orders);
    this.weeklyOrders=computeWeeklyOrders(orders);
    this.monthlyOrders=computeMonthlyOrders(orders);
    this.currentDayCount=computeCurrentDayOrders(orders);
    this.currentWeekCount=computeCurrentWeekOrders(orders);
    this.currentMonthCount=computeCurrentMonthOrders(orders);
    this.orders = orders;
  }

  private String computeBrandCount(List<OrdersDto> orders){
    Map<String, Long> brandCount = orders.stream()
        .flatMap(order -> concat(
            Optional.ofNullable(order.getOnlineOrders()).orElse(Collections.emptyList()).stream().map(CartDto::getBrandName),
            Optional.ofNullable(order.getKioskOrders()).orElse(Collections.emptyList()).stream().map(KioskDto::getBrandName),
            Optional.ofNullable(order.getOnsiteOrders()).orElse(Collections.emptyList()).stream().map(OnsiteOrderDto::getBrandName)
        ))
        .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

    return brandCount.entrySet().stream()
        .max(Map.Entry.comparingByValue())
        .map(Map.Entry::getKey)
        .orElse(null);
  }

  public static <T> Stream<T> concat(final Stream<? extends T>... streams) {
    return Arrays.stream(streams)
        .flatMap(Function.identity());
  }

  private double computeDailyOrders(List<OrdersDto> orders){
    Set<String> uniqueDates = new HashSet<>();

    long totalOrders = 0;

    for(OrdersDto order: orders){
      String orderDate = order.getDateOrdered().substring(0, 10);
      uniqueDates.add(orderDate);
      totalOrders++;
    }

    double averageDailyOrders = (double) totalOrders / uniqueDates.size();
    return Math.round(averageDailyOrders * 100.0) / 100.0;
  }

  private double computeWeeklyOrders(List<OrdersDto> orders) {
    Set<Integer> uniqueWeeks = new HashSet<>();
    long totalOrders = 0;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
    WeekFields weekFields = WeekFields.of(Locale.getDefault());

    for (OrdersDto order : orders) {
      LocalDate date = LocalDate.parse(order.getDateOrdered().substring(0, 10), formatter);
      int weekOfYear = date.get(weekFields.weekOfWeekBasedYear());
      uniqueWeeks.add(weekOfYear);
      totalOrders++;
    }

    double averageWeeklyOrders = (double) totalOrders / uniqueWeeks.size();
    return Math.round(averageWeeklyOrders * 100.0) / 100.0;
  }

  private double computeMonthlyOrders(List<OrdersDto> orders) {
    Set<Integer> uniqueMonths = new HashSet<>();
    long totalOrders = 0;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);

    for (OrdersDto order : orders) {
      LocalDate date = LocalDate.parse(order.getDateOrdered().substring(0, 10), formatter);
      int monthOfYear = date.getMonthValue();
      uniqueMonths.add(monthOfYear);
      totalOrders++;
    }

    double averageMonthlyOrders = (double) totalOrders / uniqueMonths.size();
    return Math.round(averageMonthlyOrders * 100.0) / 100.0;
  }

  private long computeCurrentDayOrders(List<OrdersDto> orders){
    SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
    String currentDate = format.format(new Date());

    long count = 0;

    for(OrdersDto order: orders){
      String orderDate = order.getDateOrdered().substring(0, 10);

      if (orderDate.equals(currentDate)){
        count++;
      }
    }

    return count;
  }

  private long computeCurrentWeekOrders(List<OrdersDto> orders){
    SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
    Calendar currentCalendar = Calendar.getInstance();
    int currentWeek = currentCalendar.get(Calendar.WEEK_OF_YEAR);
    int currentYear = currentCalendar.get(Calendar.YEAR);

    long count = 0;

    for(OrdersDto order: orders){
      try{
        Date orderDate = format.parse(order.getDateOrdered().substring(0, 10));

        Calendar orderCalendar = Calendar.getInstance();
        orderCalendar.setTime(orderDate);
        int orderWeek = orderCalendar.get(Calendar.WEEK_OF_YEAR);
        int orderYear = orderCalendar.get(Calendar.YEAR);

        if (currentWeek == orderWeek && currentYear == orderYear) {
          count++;
        }
      } catch (ParseException e){
        throw new ValidationException("Error parsing date to a data type");
      }
    }

    return count;
  }

  private long computeCurrentMonthOrders(List<OrdersDto> orders){
    SimpleDateFormat format = new SimpleDateFormat("MM");
    String currentDate = format.format(new Date());

    long count = 0;

    for(OrdersDto order: orders){
      String orderDate = order.getDateOrdered().substring(0, 2);

      if (orderDate.equals(currentDate)){
        count++;
      }
    }

    return count;
  }

  public double getDailyOrders() {
    return dailyOrders;
  }

  public void setDailyOrders(double dailyOrders) {
    this.dailyOrders = dailyOrders;
  }

  public double getWeeklyOrders() {
    return weeklyOrders;
  }

  public void setWeeklyOrders(double weeklyOrders) {
    this.weeklyOrders = weeklyOrders;
  }

  public double getMonthlyOrders() {
    return monthlyOrders;
  }

  public void setMonthlyOrders(double monthlyOrders) {
    this.monthlyOrders = monthlyOrders;
  }

  public long getCurrentDayCount() {
    return currentDayCount;
  }

  public void setCurrentDayCount(long currentDayCount) {
    this.currentDayCount = currentDayCount;
  }

  public long getCurrentWeekCount() {
    return currentWeekCount;
  }

  public void setCurrentWeekCount(long currentWeekCount) {
    this.currentWeekCount = currentWeekCount;
  }

  public long getCurrentMonthCount() {
    return currentMonthCount;
  }

  public void setCurrentMonthCount(long currentMonthCount) {
    this.currentMonthCount = currentMonthCount;
  }

  public List<OrdersDto> getOrders() {
    return orders;
  }

  public void setOrders(List<OrdersDto> orders) {
    this.orders = orders;
  }

  public String getTopBrandOrdered() {
    return topBrandOrdered;
  }

  public void setTopBrandOrdered(String topBrandOrdered) {
    this.topBrandOrdered = topBrandOrdered;
  }
}
