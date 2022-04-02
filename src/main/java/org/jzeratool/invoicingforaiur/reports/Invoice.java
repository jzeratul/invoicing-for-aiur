package org.jzeratool.invoicingforaiur.reports;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Builder
@Data
public class Invoice {

  private String invoiceNumber;
  private FromCompany fromCompany;
  private ToCompany toCompany;

  private List<Item> items;
  private BigDecimal total;

  private BigDecimal percentage;

  private LocalDate from;
  private LocalDate to;
  private LocalDate expire;
  private LocalDate created;

  private String title;
  private String note;
}
