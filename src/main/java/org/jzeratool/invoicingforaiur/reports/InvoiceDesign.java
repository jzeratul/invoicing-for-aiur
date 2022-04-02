package org.jzeratool.invoicingforaiur.reports;

import net.sf.dynamicreports.jasper.builder.JasperReportBuilder;
import net.sf.dynamicreports.jasper.builder.export.JasperPdfExporterBuilder;
import net.sf.dynamicreports.report.builder.column.TextColumnBuilder;
import net.sf.dynamicreports.report.builder.component.ComponentBuilder;
import net.sf.dynamicreports.report.builder.component.HorizontalListBuilder;
import net.sf.dynamicreports.report.builder.style.StyleBuilder;
import net.sf.dynamicreports.report.constant.HorizontalTextAlignment;
import net.sf.dynamicreports.report.exception.DRException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

import static net.sf.dynamicreports.report.builder.DynamicReports.cmp;
import static net.sf.dynamicreports.report.builder.DynamicReports.col;
import static net.sf.dynamicreports.report.builder.DynamicReports.export;
import static net.sf.dynamicreports.report.builder.DynamicReports.report;
import static net.sf.dynamicreports.report.builder.DynamicReports.stl;
import static net.sf.dynamicreports.report.builder.DynamicReports.type;

@Service
public class InvoiceDesign {

  private static final DateTimeFormatter SDF = DateTimeFormatter.ofPattern("dd MMM YYYY");

  private InvoiceData data = new InvoiceData();

  public static void main(String[] args) {
    InvoiceDesign design = new InvoiceDesign();
    try {
      JasperReportBuilder report = design.build(new InvoiceData());

      JasperPdfExporterBuilder pdfExporter = export.pdfExporter(design.data.getFileName());
      report.toPdf(pdfExporter);

      report.show();
    } catch (DRException e) {
      e.printStackTrace();
    }
  }

  public JasperReportBuilder build(InvoiceData data) throws DRException {
    JasperReportBuilder report = report();

    // init styles
    StyleBuilder columnStyle = stl.style(Templates.columnStyle);
    StyleBuilder totalStyle = stl.style(Templates.boldStyle).setTopPadding(50).setHorizontalTextAlignment(HorizontalTextAlignment.RIGHT);

    // init columns
    TextColumnBuilder<String> descriptionColumn = col.column("Omschrijving", "description", type.stringType())
            .setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);
    TextColumnBuilder<BigDecimal> quantityColumn = col.column("Omzet", "quantity", Templates.currencyType)
            .setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);
    TextColumnBuilder<BigDecimal> percentageColumn = col.column("Percentage", "percentage", Templates.percentageType)
            .setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);

    // total = price + vat
    TextColumnBuilder<BigDecimal> totalColumn = quantityColumn.multiply(percentageColumn)
            .setTitle("Totaal")
            .setDataType(Templates.currencyType)
            .setHorizontalTextAlignment(HorizontalTextAlignment.CENTER);

    // configure report
    report.setTemplate(Templates.reportTemplate)
            .setColumnStyle(columnStyle)
            // columns
            .columns(descriptionColumn, quantityColumn, percentageColumn, totalColumn)
            // band components
            .title(Templates.createTitleComponent(data.getInvoice().getTitle()), cmp.horizontalList()
                            .setStyle(stl.style(10))
                            .setGap(70)
                            .add(cmp.hListCell(createToComponent("Aan", data.getInvoice().getToCompany())).heightFixedOnTop(),
                                    cmp.hListCell(createEmptyComponent()).heightFixedOnTop(),
                                    cmp.hListCell(createFromComponent("Van", data.getInvoice().getFromCompany())).heightFixedOnTop())
                            .newRow()
                            .setStyle(stl.style(15))
                            .add(cmp.hListCell(createMetaComponent(data.getInvoice())).heightFixedOnTop())
                            .newRow()
                            .setStyle(stl.style(15)),
                    cmp.verticalGap(10))
            .pageFooter(Templates.footerComponent)
            .summary(
                    cmp.text(data.getInvoice().getTotal()).setValueFormatter(Templates.createCurrencyValueFormatter("Totaal:      ")).setStyle(totalStyle),

                    cmp.text(data.getInvoice().getNote())
                            .setStyle(stl.style(10))
                            .setHorizontalTextAlignment(HorizontalTextAlignment.CENTER)

            )
            .setDataSource(data.createDataSource());

    return report;
  }

  private ComponentBuilder<?, ?> createToComponent(String label, ToCompany c) {
    HorizontalListBuilder list = cmp.horizontalList().setBaseStyle(stl.style().setTopBorder(stl.penThin()).setLeftPadding(10));
    addAttribute(list, c.getName());
    addAttribute(list, c.getAddress());
    addAttribute(list, c.getPostalCode() + " " + c.getCity());
    addAttribute(list, c.getRegionOrCountry());
    return cmp.verticalList(cmp.text(label), list);
  }

  private ComponentBuilder<?, ?> createFromComponent(String label, FromCompany c) {
    HorizontalListBuilder list = cmp.horizontalList().setBaseStyle(stl.style().setTopBorder(stl.penThin()).setLeftPadding(10));
    addAttribute(list, c.getName());
    addAttribute(list, c.getAddress());
    addAttribute(list, c.getPostalCode() + " " + c.getCity());
    addAttribute(list, c.getIdentificationNumber());
    addAttribute(list, c.getBankAccount());
    return cmp.verticalList(cmp.text(label), list);
  }

  private ComponentBuilder<?, ?> createMetaComponent(Invoice inv) {
    HorizontalListBuilder list = cmp.horizontalList();

    list.add(
            cmp.horizontalList().add(cmp.text("Factuurnummer")).newRow().add(cmp.text(inv.getInvoiceNumber()))
    );
    list.add(
            cmp.horizontalList().add(cmp.text("Periode van"))
                    .newRow()
                    .add(cmp.text(SDF.format(inv.getFrom())))
    );
    list.add(
            cmp.horizontalList().add(cmp.text("Periode tot"))
                    .newRow()
                    .add(cmp.text(SDF.format(inv.getTo())))
    );
    list.add(
            cmp.horizontalList().add(cmp.text("Vervalddatum"))
                    .newRow()
                    .add(cmp.text(SDF.format(inv.getExpire())))
    );
    list.add(
            cmp.horizontalList().add(cmp.text("Factuurdatum"))
                    .newRow()
                    .add(cmp.text(SDF.format(inv.getCreated())))
    );

    return cmp.verticalList(list);
  }

  private ComponentBuilder<?, ?> createEmptyComponent() {
    return cmp.horizontalList();
  }

  private void addAttribute(HorizontalListBuilder list, String value) {
    if (value != null) {
      list.add(cmp.text(value)).newRow();
    }
  }
}
