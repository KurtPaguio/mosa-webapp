package com.example.mosawebapp.kiosk;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.stereotype.Service;

@Service
public class QueueingNumberService {
  private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");
  private static AtomicInteger currentNumber = new AtomicInteger(0);
  private static String currentDate = DATE_FORMAT.format(new Date());

  public static synchronized String getNextQueueingNumber() {
    String today = DATE_FORMAT.format(new Date());
    if (!today.equals(currentDate)) {
      currentDate = today;
      currentNumber.set(0);
    }
    return String.format("%05d", currentNumber.incrementAndGet());
  }
}