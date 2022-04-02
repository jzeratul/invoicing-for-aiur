/*
 * DynamicReports - Free Java reporting library for creating reports dynamically
 *
 * Copyright (C) 2010 - 2018 Ricardo Mariaca and the Dynamic Reports Contributors
 *
 * This file is part of DynamicReports.
 *
 * DynamicReports is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * DynamicReports is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with DynamicReports. If not, see <http://www.gnu.org/licenses/>.
 */
package org.jzeratool.invoicingforaiur.reports;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class InvoiceData {

  private static final DateTimeFormatter SDF2 = DateTimeFormatter.ofPattern("ddMMMYYYY");

  private Invoice invoice;

  public InvoiceData() {
    invoice = createTestInvoice();
  }

  public InvoiceData(Invoice realInvoice) {
    invoice = realInvoice;
  }

  public String getFileName() {
    return invoice.getToCompany().getName().replaceAll(" ", "") + "-" +
            SDF2.format(invoice.getCreated()) + ".pdf";
  }

  private Invoice createTestInvoice() {

    return Invoice.builder()
            .invoiceNumber("2020020")
            .fromCompany(createFromCompany(FromCompany.builder().identificationNumber("asdf").bankAccount("IBAN:test").address("test").name("name").postalCode("test").city("test").build()))
            .toCompany(createToCompany("TEST", "TEST 6", "TEST", "TEST", "TEST"))
            .from(LocalDate.now())
            .to(LocalDate.now())
            .expire(LocalDate.now())
            .created(LocalDate.now())
            .total(new BigDecimal("2724.50"))
            .items(
                    Arrays.asList(createItem("Service Provided", new BigDecimal("5449.00"), new BigDecimal("0.5")))
            )
            .build();
  }

  public static ToCompany createToCompany(String name, String address, String city, String postalCode, String region) {
    return ToCompany.builder()
            .name(name)
            .address(address)
            .city(city)
            .postalCode(postalCode)
            .regionOrCountry(region)
            .build();
  }

  public static FromCompany createFromCompany(FromCompany fromCompany) {
    return FromCompany.builder()
            .name(fromCompany.getName())
            .address(fromCompany.getAddress())
            .city(fromCompany.getCity())
            .postalCode(fromCompany.getPostalCode())
            .identificationNumber(fromCompany.getIdentificationNumber())
            .bankAccount(fromCompany.getBankAccount())
            .build();
  }

  public static Item createItem(String description, BigDecimal quantity, BigDecimal percentage) {
    Item item = Item.builder()
            .description(description)
            .quantity(quantity)
            .percentage(percentage)
            .build();
    return item;
  }

  public Invoice getInvoice() {
    return invoice;
  }

  public JRDataSource createDataSource() {
    return new JRBeanCollectionDataSource(invoice.getItems());
  }
}
