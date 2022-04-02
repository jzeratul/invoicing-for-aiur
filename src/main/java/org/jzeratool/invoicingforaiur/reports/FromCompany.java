package org.jzeratool.invoicingforaiur.reports;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FromCompany {
  private String name;
  private String address;
  private String city;
  private String postalCode;
  private String bankAccount;
  private String identificationNumber;
}
