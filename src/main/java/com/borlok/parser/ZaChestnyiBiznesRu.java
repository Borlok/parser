package com.borlok.parser;

import com.borlok.model.Company;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class ZaChestnyiBiznesRu {
    private static final String startUrl = "https://zachestnyibiznes.ru";
    private static final Set<Company> companies = new HashSet<>();

    public static void parse() throws IOException {
//        String inputUrl = startUrl + "/search?query=респ+Татарстан";
//
//        Document document = Jsoup
//                .connect(inputUrl)
//                .headers(fillHeaders())
//                .get();
//
//        Elements itemProp = document.select("a[itemprop]");
//        for (Element element : itemProp)
//            createCompany(element.attr("href"));

        createCompany("/company/ul/1041621022574_1655083578_UPRAVLENIE-ROSKOMNADZORA-PO-RESPUBLIKE-TATARSTAN-TATARSTAN");

        for (Company company : companies)
            System.out.println(company);
    }

    private static void createCompany(String href) throws IOException {
        Company company;
        try {
            if (href.length() <= 1)
                return;
            Document document = Jsoup
                    .connect(startUrl + href)
                    .headers(fillHeaders())
                    .get();

            Elements companyNameElement = document.select("span#nameCompCard"); //Название компании
            Elements statusElement = document.select("div.m-t-5").select("b[class]"); // статус
            Elements registerDateElement = document.select("div.m-t-5").select("b[itemprop]"); // дата регистрации
            Elements innElement = document.select("span#inn"); // ИНН
            Elements addressElement = document.select("div[itemprop = address]"); // адрес
            Elements directorNameElement = document.select("div[itemtype=http://schema.org/OrganizationRole]").select("a[target=_blank]"); // Имя директора
            Elements founderElementNum = document.select("a[data-target=#modal-founders]");// Имя Учредителя
            Elements foundersElement = document.select("table.hidden-print").select("span[itemprop=founder]");


            String companyName = getStringFromElement(companyNameElement);
            String status = getStringFromElement(statusElement);
            String date = getDateFromElement(registerDateElement);
            int inn = Integer.parseInt(getStringFromElement(innElement));
            String address = getAddress(addressElement);
            System.out.print(href + " ");
            String director = getDirectorsName(directorNameElement);
            int foundersNumber = Integer.parseInt(founderElementNum.first().text());
            List<String> founders = getFounders(document,foundersElement, foundersNumber);
            System.out.print(director + " ");



            System.out.println(" Соответствие соочередителей: " + (founders.size() == foundersNumber));
            company = new Company(inn, director, founders, address, companyName, date, status, "UNKNOWN");
        } catch (Exception e) {
            return;
        }
        companies.add(company);
    }

    private static List<String> getFounders(Document document, Elements foundersElement, int foundersNumber) {
        List<String> founders = new ArrayList<>();

        if (foundersNumber != 0) {
            if (foundersElement.textNodes().size() <= 0) {
                foundersElement = document.select("table.hidden-print").select("a[target]");
                for (int i = 0; i < foundersElement.textNodes().size(); i = i + 2)
                    founders.add(foundersElement.get(i).text());
            } else
                return foundersElement.stream().map(x -> x + "").collect(Collectors.toList());
        }
        return founders;
    }

    private static String getAddress(Elements addressElement) {
        return Arrays.stream(addressElement.text().split(" ")).skip(4).reduce("", (s, s2) -> s + " " + s2);
    }

    private static String getDateFromElement(Elements registerDateElement) {
        return registerDateElement.text().split(" ")[4];
    }

    private static String getStringFromElement(Elements element) {
        return element.text();
    }

    private static String getDirectorsName(Elements directorNameElement) {
        //TODO Сделать что нибудь с компанией вместо физ лица
        try {
            return directorNameElement.get(0).text();
        } catch (IndexOutOfBoundsException e) {
            return  "Company";
        }
    }

    private static Map<String, String> fillHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.90 Safari/537.36");
        headers.put("cf-request-id", "08ffe74e60000015fce9af0000000001");
        headers.put("Referer", "https://zachestnyibiznes.ru/");
        headers.put("sec-fetch-dest", "document");
        headers.put("sec-fetch-mode", "navigate");
        headers.put("sec-fetch-site", "same-origin");
        headers.put("sec-fetch-user", "?1");
        headers.put("upgrade-insecure-request", "1");
        return headers;
    }
}
