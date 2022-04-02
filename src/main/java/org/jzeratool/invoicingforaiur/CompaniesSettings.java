package org.jzeratool.invoicingforaiur;

import lombok.Data;
import org.jzeratool.invoicingforaiur.reports.FromCompany;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "settings")
public class CompaniesSettings {

  private String serviceProvided;
  private String invoiceTitle;
  private String invoiceNote;
  private Map<String, Map<String, Object>> companies;
  private FromCompany fromCompany;
}
