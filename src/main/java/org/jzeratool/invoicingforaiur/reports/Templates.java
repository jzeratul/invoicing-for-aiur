package org.jzeratool.invoicingforaiur.reports;

import net.sf.dynamicreports.report.base.expression.AbstractValueFormatter;
import net.sf.dynamicreports.report.builder.ReportTemplateBuilder;
import net.sf.dynamicreports.report.builder.component.ComponentBuilder;
import net.sf.dynamicreports.report.builder.datatype.BigDecimalType;
import net.sf.dynamicreports.report.builder.style.StyleBuilder;
import net.sf.dynamicreports.report.builder.tableofcontents.TableOfContentsCustomizerBuilder;
import net.sf.dynamicreports.report.constant.HorizontalTextAlignment;
import net.sf.dynamicreports.report.constant.VerticalTextAlignment;
import net.sf.dynamicreports.report.definition.ReportParameters;

import java.util.Locale;

import static net.sf.dynamicreports.report.builder.DynamicReports.cmp;
import static net.sf.dynamicreports.report.builder.DynamicReports.stl;
import static net.sf.dynamicreports.report.builder.DynamicReports.tableOfContentsCustomizer;
import static net.sf.dynamicreports.report.builder.DynamicReports.template;

public class Templates {

  public static final StyleBuilder rootStyle;
  public static final StyleBuilder boldStyle;
  public static final StyleBuilder italicStyle;
  public static final StyleBuilder boldCenteredStyle;
  public static final StyleBuilder columnStyle;
  public static final StyleBuilder columnTitleStyle;
  public static final StyleBuilder groupStyle;
  public static final StyleBuilder subtotalStyle;
  public static final ReportTemplateBuilder reportTemplate;
  public static final CurrencyType currencyType;
  public static final PercentageType percentageType;
  public static final ComponentBuilder<?, ?> footerComponent;

  static {
    rootStyle = stl.style().setFontSize(10).setPadding(2);
    boldStyle = stl.style(rootStyle).bold();
    italicStyle = stl.style(rootStyle).italic();
    boldCenteredStyle = stl.style(boldStyle).setTextAlignment(HorizontalTextAlignment.CENTER, VerticalTextAlignment.MIDDLE);
    columnStyle = stl.style(rootStyle).setVerticalTextAlignment(VerticalTextAlignment.MIDDLE);
    columnTitleStyle = stl.style(columnStyle).setBottomBorder(stl.penThin()).setHorizontalTextAlignment(HorizontalTextAlignment.CENTER).bold();
    groupStyle = stl.style(boldStyle).setHorizontalTextAlignment(HorizontalTextAlignment.LEFT);
    subtotalStyle = stl.style(boldStyle).setTopBorder(stl.pen1Point());

    TableOfContentsCustomizerBuilder tableOfContentsCustomizer = tableOfContentsCustomizer();

    reportTemplate = template().setLocale(Locale.ENGLISH)
            .setColumnStyle(columnStyle)
            .setCrosstabCellStyle(columnStyle)
            .setColumnTitleStyle(columnTitleStyle)
            .setGroupStyle(groupStyle)
            .setGroupTitleStyle(groupStyle)
            .setSubtotalStyle(subtotalStyle)
            .setTableOfContentsCustomizer(tableOfContentsCustomizer);

    currencyType = new CurrencyType();
    percentageType = new PercentageType();

    footerComponent = cmp.pageXofY().setStyle(stl.style(boldCenteredStyle).setTopBorder(stl.pen1Point()));
  }

  public static ComponentBuilder<?, ?> createTitleComponent(String label) {
    return cmp.horizontalList()
            .add(cmp.text(label).setStyle(boldCenteredStyle).setHorizontalTextAlignment(HorizontalTextAlignment.CENTER))
            .newRow()
            .add(cmp.verticalGap(10));
  }

  public static CurrencyValueFormatter createCurrencyValueFormatter(String label) {
    return new CurrencyValueFormatter(label);
  }

  public static class CurrencyType extends BigDecimalType {
    @Override
    public String getPattern() {
      return "EUR #,###.00";
    }
  }

  private static class CurrencyValueFormatter extends AbstractValueFormatter<String, Number> {
    private final String label;

    public CurrencyValueFormatter(String label) {
      this.label = label;
    }

    @Override
    public String format(Number value, ReportParameters reportParameters) {
      return label + currencyType.valueToString(value, reportParameters.getLocale());
    }
  }

  public static class PercentageType extends BigDecimalType {
    @Override
    public String getPattern() {
      return "##.00 %";
    }
  }
}
