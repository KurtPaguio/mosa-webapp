package com.example.mosawebapp.kiosk.dto;

import com.example.mosawebapp.kiosk.domain.KioskCheckout;
import com.example.mosawebapp.kiosk.domain.KioskOrder;
import com.example.mosawebapp.utils.DateTimeFormatter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(Include.NON_NULL)
public class KioskCheckoutDto {
  private String checkoutId;
  private String dateCheckout;
  private KioskDto kiosk;

  public KioskCheckoutDto(){}

  public KioskCheckoutDto(String checkoutId, String dateCheckout, KioskDto kiosk) {
    this.checkoutId = checkoutId;
    this.dateCheckout = dateCheckout;
    this.kiosk = kiosk;
  }

  public static KioskCheckoutDto buildFromEntity(KioskCheckout kioskCheckout){
    return new KioskCheckoutDto(kioskCheckout.getId(), DateTimeFormatter.get_MMDDYYY_Format(kioskCheckout.getDateCreated()),
        KioskDto.buildFromEntity(kioskCheckout.getKiosk()));
  }

  public static KioskCheckoutDto buildFromEntityV2(KioskCheckout kioskCheckout, List<KioskOrder> orders){
    return new KioskCheckoutDto(kioskCheckout.getId(), DateTimeFormatter.get_MMDDYYY_Format(kioskCheckout.getDateCreated()),
        new KioskDto(kioskCheckout.getKiosk(), orders));
  }

  public static List<KioskCheckoutDto> buildFromEntities(List<KioskCheckout> checkouts){
    List<KioskCheckoutDto> dto = new ArrayList<>();

    for(KioskCheckout kioskCheckout: checkouts){
      dto.add(buildFromEntity(kioskCheckout));
    }

    return dto;
  }

  public String getCheckoutId() {
    return checkoutId;
  }

  public void setCheckoutId(String checkoutId) {
    this.checkoutId = checkoutId;
  }

  public String getDateCheckout() {
    return dateCheckout;
  }

  public void setDateCheckout(String dateCheckout) {
    this.dateCheckout = dateCheckout;
  }

  public KioskDto getKiosk() {
    return kiosk;
  }

  public void setKiosk(KioskDto kiosk) {
    this.kiosk = kiosk;
  }
}
