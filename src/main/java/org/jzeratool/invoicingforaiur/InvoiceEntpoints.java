package org.jzeratool.invoicingforaiur;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.dynamicreports.jasper.builder.JasperReportBuilder;
import net.sf.dynamicreports.report.exception.DRException;
import org.jzeratool.invoicingforaiur.reports.Invoice;
import org.jzeratool.invoicingforaiur.reports.InvoiceData;
import org.jzeratool.invoicingforaiur.reports.InvoiceDesign;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.jzeratool.invoicingforaiur.reports.InvoiceData.createFromCompany;
import static org.jzeratool.invoicingforaiur.reports.InvoiceData.createItem;
import static org.jzeratool.invoicingforaiur.reports.InvoiceData.createToCompany;

@Slf4j
@RestController
@RequiredArgsConstructor
public class InvoiceEntpoints {
  private static final DateTimeFormatter INVOICENR = DateTimeFormatter.ofPattern("ddMMYYYYHHmm");

  private final InvoiceDesign invoiceDesign;

  private final CompaniesSettings companiesSettings;

  @GetMapping(path = "/api/v1/settings", produces = "application/json")
  public ResponseEntity<InvoiceSettings> getInvoiceSettings() {

    Integer year = LocalDate.now().getYear();
    List<InvoiceSettings.MonthlyInvoice> mi = new ArrayList<>(12);

    for (int i = 0; i < 12; i++) {
      var startDate = LocalDate.of(year, i + 1, 1);
      var endDate = LocalDate.of(year, i + 1, startDate.lengthOfMonth());

      InvoiceSettings.MonthlyInvoice inv = InvoiceSettings.MonthlyInvoice.builder()
              .startDate(startDate)
              .endDate(endDate)
              .expirationDate(endDate.plusWeeks(2))
              .creationDate(endDate)
              .build();
      mi.add(inv);
    }

    InvoiceSettings invoiceSettings = InvoiceSettings.builder()
            .companiesSettings(companiesSettings)
            .year(year)
            .monthlyInvoices(mi)
            .build();

    return ResponseEntity.ok(invoiceSettings);
  }

  @GetMapping(path = "/api/v1/invoice", produces = "application/pdf")
  public ResponseEntity<?> getInvoiceForAiur(@RequestParam Map<String, String> parameters) {

    System.out.println(parameters);

    LocalDate from;
    LocalDate to;
    LocalDate expires;
    LocalDate created;

    BigDecimal omzet = BigDecimal.ZERO;
    BigDecimal income = BigDecimal.ZERO;
    BigDecimal percent;
    BigDecimal finalAmount;

    String company;
    String address;

    try {
      from = LocalDate.parse(parameters.get("invoiceFromDate"));
    } catch (Exception e) {
      return ResponseEntity.badRequest().body("invalid invoiceFromDate: " + e.getMessage() + " expecting YYYY-MM-DD");
    }
    try {
      to = LocalDate.parse(parameters.get("invoiceToDate"));
    } catch (Exception e) {
      return ResponseEntity.badRequest().body("invalid invoiceToDate: " + e.getMessage() + " expecting YYYY-MM-DD");
    }

    if (from.isAfter(to)) {
      return ResponseEntity.badRequest().body("invalid from-to: expecting the from date to be before the to date");
    }

    try {
      expires = LocalDate.parse(parameters.get("invoiceExpirationDate"));
    } catch (Exception e) {
      expires = to.plusWeeks(2);
    }
    try {
      created = LocalDate.parse(parameters.get("invoiceCreatedDate"));
    } catch (Exception e) {
      created = from.minusWeeks(3);
    }

    String companyParam = parameters.get("invoiceCompany");
    if (companyParam == null || companyParam.isBlank()) {
      return ResponseEntity.badRequest().body("invalid invoiceCompany: expecting one of " + "addresses.keySet()");
    } else {
      company = companyParam;
    }

    String addressParam = parameters.get("addressCompany");
    if (addressParam == null || addressParam.isBlank()) {
      address = companiesSettings.getCompanies().get(company).get("address").toString();
    } else {
      address = addressParam;
    }

    String amountParam = parameters.get("invoiceAmount");
    if (amountParam == null || amountParam.equals("0")) {

      String incomeParam = parameters.get("income");
      if (incomeParam == null || incomeParam.equals("0")) {
        return ResponseEntity.badRequest().body("invalid income: expecting a non null/empty amount");
      }
      try {
        income = new BigDecimal(incomeParam);
      } catch (Exception e) {
        return ResponseEntity.badRequest().body("invalid income: expecting a number");
      }
    } else {
      try {
        omzet = new BigDecimal(amountParam);
      } catch (Exception e) {
        return ResponseEntity.badRequest().body("invalid invoiceAmount: expecting a number");
      }
    }

    String percentParam = parameters.get("invoicePercent");
    if (percentParam == null || percentParam.isBlank()) {
      return ResponseEntity.badRequest().body("invalid invoicePercent: expecting a number");
    }
    try {
      percent = new BigDecimal(percentParam);
    } catch (Exception e) {
      return ResponseEntity.badRequest().body("invalid invoicePercent: expecting a number");
    }

    if (omzet.compareTo(BigDecimal.ZERO) == 0 && income.compareTo(BigDecimal.ZERO) == 1) {
      finalAmount = income;
      omzet = finalAmount.divide(percent, 2, RoundingMode.HALF_UP);
    } else {
      finalAmount = omzet.multiply(percent);
    }

    String addrPoints[] = address.split(",");

    Invoice invoice = Invoice.builder()
            .invoiceNumber(INVOICENR.format(LocalDateTime.now().with(created)))
            .fromCompany(createFromCompany(companiesSettings.getFromCompany()))
            .toCompany(createToCompany(companiesSettings.getCompanies().get(company).get("name").toString(), addrPoints[0], addrPoints[1], addrPoints[2], addrPoints[3]))
            .from(from)
            .to(to)
            .expire(expires)
            .created(created)
            .total(finalAmount)
            .title(companiesSettings.getInvoiceTitle())
            .note(companiesSettings.getInvoiceNote())
            .items(
                    Arrays.asList(createItem(companiesSettings.getServiceProvided(), omzet, percent))
            )
            .build();

    log.info("Invoice built from the request: \n" +
                    "from {} \n" +
                    "to {} \n" +
                    "expires {} \n" +
                    "created {} \n" +
                    "company {} \n" +
                    "address {} \n" +
                    "amount {} \n" +
                    "income {} \n" +
                    "percent {} \n" +
                    "finalAmount {} \n",
            from,
            to,
            expires,
            created,
            company,
            address,
            omzet,
            income,
            percent,
            finalAmount
    );
    try {
      InvoiceData data = new InvoiceData(invoice);
      JasperReportBuilder reportBuilder = invoiceDesign.build(data);
      ByteArrayOutputStream os = new ByteArrayOutputStream();

      reportBuilder.toPdf(os).build();

      return ResponseEntity.ok()
              .header("Content-Disposition", "attachment; filename=\"" + data.getFileName() + "\"")
              .body(new ByteArrayResource(os.toByteArray(), data.getFileName()))
              ;
    } catch (DRException e) {
      return ResponseEntity.internalServerError().body(e.getMessage());
    }
  }
}
