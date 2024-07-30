package org.example;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;

public class Main {
    private static final int TOTAL_RECORDS = 1000000;

    public static void main(String[] args) {
        Workbook workbook = new SXSSFWorkbook();
        Sheet sheet = workbook.createSheet("Customers");

        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("ID");
        headerRow.createCell(1).setCellValue("Name");
        headerRow.createCell(2).setCellValue("LastName");
        headerRow.createCell(3).setCellValue("Address");
        headerRow.createCell(4).setCellValue("Email");

        for (int i = 1; i <= TOTAL_RECORDS; i++) {
            Row row = sheet.createRow(i);
            row.createCell(0).setCellValue(i);
            row.createCell(1).setCellValue("name" + i);
            row.createCell(2).setCellValue("lastName" + i);
            row.createCell(3).setCellValue("address" + i);
            row.createCell(4).setCellValue("email" + i);
        }

        try (FileOutputStream fileOut = new FileOutputStream("../customers.xlsx")) {
            workbook.write(fileOut);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (workbook instanceof SXSSFWorkbook) {
                ((SXSSFWorkbook) workbook).dispose();
            }
        }
    }
}