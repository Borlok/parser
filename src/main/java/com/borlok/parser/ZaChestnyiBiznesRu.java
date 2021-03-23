package com.borlok.parser;

import com.borlok.model.Company;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class ZaChestnyiBiznesRu {
    private static final String startUrl = "https://zachestnyibiznes.ru";
    private static final Set<Company> companies = new HashSet<>();
    private static int companyNumber; //TODO вконце удалить

    public static void parse() throws IOException {
        int count = 2;
        int i = 1;
        while (i != count) {

            String inputUrl = startUrl + "/search?query=респ+Татарстан&page=" + i;

            Document document = Jsoup
                    .connect(inputUrl)
                    .headers(fillHeaders())
                    .get();

            Elements itemProp = document.select("a[itemprop]");
            for (Element element : itemProp)
                createCompany(element.attr("href"));
            i++;
        }
//        createCompany("/company/ul/1051622166848_1655102728_OOO-ROSINTER-RESTORANTS-TATARSTAN");

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
//            System.out.print(href + " "); //TODO
            String director = getDirectorsName(directorNameElement, document);
            int foundersNumber = Integer.parseInt(founderElementNum.first().text());
            List<String> founders = getFounders(document, foundersElement, foundersNumber);
//            System.out.println(director + " ");
//            System.out.println(); //TODO


            company = new Company(inn, director, founders, address, companyName, date, status, "UNKNOWN");
        } catch (Exception e) {
            return;
        }
        companies.add(company);
    }

    private static List<String> getFounders(Document document, Elements foundersElement, int foundersNumber) {
        Set<String> founders = new HashSet<>();
        if (foundersNumber != 0) {
            for (int i = 0; i < foundersElement.textNodes().size(); i++)
                founders.add(foundersElement.get(i).text());
            if (foundersElement.textNodes().isEmpty() || foundersElement.textNodes().size() != foundersNumber) {
                foundersElement = document.select("table.hidden-print").select("a[target]");
                for (int i = 0; i < foundersElement.textNodes().size(); i = i + 2)
                    founders.add(foundersElement.get(i).text());
            }
            if (founders.isEmpty() || founders.size() != foundersNumber) {
                //TODO здесь объединить на конечном этапе
                foundersElement = document.select("table.hidden-print").select("td");
                founders = foundersElement.textNodes().stream()
                        .filter(x -> !(x.text().matches("По данным портала ЗАЧЕСТНЫЙБИЗНЕС"))
                                && !x.text().equals(" ")
                                && !x.text().equals("")
                                && !x.text().matches("\\d*%")
                                && !x.text().matches("\\d*"))
                        .map(x -> "" + x.text().trim())
                        .collect(Collectors.toSet());
            }
        }
        System.out.print(++companyNumber + " Соответствие соочередителей: " + foundersNumber + " " + (founders.size() == foundersNumber) + " "); //TODO
        System.out.println(); //TODO
        return new ArrayList<>(founders);
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

    private static String getDirectorsName(Elements directorNameElement, Document document) {
        //TODO Сделать что нибудь с компанией вместо физ лица
        try {
            return directorNameElement.get(0).text();
        } catch (IndexOutOfBoundsException e) {
            //TODO здесь объединить на конечном этапе
            Elements elements = document
                    .select("div.m-t-15:contains(Управляющая организация)")
                    .select("a[target=_blank]");
            return elements.textNodes().get(0).text();
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
