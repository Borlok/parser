package com.borlok.util;

import com.borlok.model.Company;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

public class XmlWriter {
    private File pathToXml;
    private File pathFromTxt;

    public XmlWriter(File pathToXml, File pathFromTxt) {
        this.pathToXml = pathToXml;
        this.pathFromTxt = pathFromTxt;
    }

    public XmlWriter(File pathToXml) {
        this.pathToXml = pathToXml;
    }

    public void writeCompanyToXml (List<Company> companies) throws IOException {
        HSSFWorkbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        int i = 0;
        while (i < companies.size()) {
            Row row = sheet.createRow(i);
            row.createCell(0).setCellValue(companies.get(i).getInn());
            row.createCell(1).setCellValue(companies.get(i).getDirector());
            row.createCell(2).setCellValue(companies.get(i).getFounders().toString());
            row.createCell(3).setCellValue(companies.get(i).getAddress());
            row.createCell(4).setCellValue(companies.get(i).getCompanyName());
            row.createCell(5).setCellValue(companies.get(i).getRegistrationDate());
            row.createCell(6).setCellValue(companies.get(i).getStatus());
            row.createCell(7).setCellValue(companies.get(i).getTax());

            FileOutputStream outputStream = new FileOutputStream(pathToXml);
            outputStream.write(workbook.getBytes());
            i++;
            outputStream.close();
        }
        workbook.close();
    }

    public void writeFromTxtToXml () {

    }
}
