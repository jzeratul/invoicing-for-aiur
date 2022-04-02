package org.jzeratool.invoicingforaiur.reports;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class Item {
  private String description;
  private BigDecimal quantity;
  private BigDecimal percentage;
}
