package org.jzeratool.invoicingforaiur;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class InvoiceSettings {

  private CompaniesSettings companiesSettings;
  private Integer year;
  private List<MonthlyInvoice> monthlyInvoices;

  @Data
  @Builder
  public static class MonthlyInvoice {
    LocalDate startDate; // usually the 1st of the month
    LocalDate endDate; // usually the end of the month
    LocalDate expirationDate; // usually endDate + 2 weeks
    LocalDate creationDate; // usually startDate + 2 weeks
  }
}
