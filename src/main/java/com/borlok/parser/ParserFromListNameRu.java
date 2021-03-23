package com.borlok.parser;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;

public class ParserFromListNameRu {
    public static void parse() throws IOException {
        File file = new File("url.txt");
        File xml = new File("xml.xls");
        Scanner scanner = new Scanner(file);
        String inputUrl = "";
        int rowNum = 1;
        HSSFWorkbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        while (scanner.hasNext()) {
            inputUrl = scanner.next();

            Document doc = Jsoup
                    .connect(inputUrl)
                    .header("User-Agent", "Chrome/81.0.4044.138").get();

            Elements h1 = doc.select("h1");
            Elements div = doc.select("div.entry");
            Elements text = div.select("div.box, p");

            Row row = sheet.createRow(rowNum);

            Cell url = row.createCell(0);
            url.setCellValue(inputUrl);
            Cell name = row.createCell(1);
            name.setCellValue(h1.toString());
            Cell body = row.createCell(2);
            body.setCellValue(text.toString());

            System.out.println(rowNum + " " + inputUrl);
            FileOutputStream outputStream = new FileOutputStream(xml);
            outputStream.write(workbook.getBytes());
            rowNum++;
            outputStream.close();
        }
        workbook.close();
    }
}
