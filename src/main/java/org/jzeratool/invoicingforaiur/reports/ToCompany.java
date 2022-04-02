package org.jzeratool.invoicingforaiur.reports;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ToCompany {
  private String name;
  private String address;
  private String city;
  private String postalCode;
  private String regionOrCountry;
}
