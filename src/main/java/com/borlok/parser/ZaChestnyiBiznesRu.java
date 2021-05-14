package com.borlok.parser;

import com.borlok.util.Utils;
import com.borlok.model.Company;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
//TODO Дописать вид деятельности

/**
 * Парсер для сайта https://zachestnyibiznes.ru
 */


public class ZaChestnyiBiznesRu {
    private static final String HTTPS_ZACHESTNYIBIZNES_RU = "https://zachestnyibiznes.ru";
    private static final File PATH_TO_XML = new File("xml2.xls");
    private static final Utils UTILS = new Utils(PATH_TO_XML);
    private static final Set<Company> COMPANIES = new HashSet<>();
    private static int companyNumber; //TODO вконце удалить

    public static void parse(String searchQuery) throws IOException {
        String query = UTILS.getQuery(searchQuery);
        int pageValue = 2;
        int counter = 1;
        while (counter != pageValue) {

            String urlForParsing = HTTPS_ZACHESTNYIBIZNES_RU + "/search?query=" + query + "&page=" + counter;
            Document document = getDocument(urlForParsing);

            Elements referencesToCompanyPage = document.select("a[itemprop]");

            for (Element element : referencesToCompanyPage)
                createCompany(element.attr("href"));

            counter++;
        }

        saveCollectionToXml();
//        createCompany("/company/ul/1021600003083_1659005563_TRO-VDPO-RESPUBLIKI-TATARSTAN");
        for (Company company : COMPANIES)
            System.out.println(company);
    }

    private static Document getDocument(String urlForParsing) throws IOException {
        return Jsoup.connect(urlForParsing).headers(getHeaders()).get();
    }

    private static void saveCollectionToXml() {
        try {
            UTILS.writeCompanyToXml(new ArrayList<>(COMPANIES));
        } catch (IOException e) {
            System.err.println("Something wrong by save collection: " + e);
        }
    }

    private static void createCompany(String href) {
        Company company;
        try {
            if (href.length() <= 1)
                return;
            Document document = getDocument(HTTPS_ZACHESTNYIBIZNES_RU + href);

            Elements companyNameElement = document.select("span#nameCompCard");
            Elements statusElement = document.select("div.m-t-5").select("b[class]");
            Elements registerDateElement = document.select("div.m-t-5").select("b[itemprop]");
            Elements innElement = document.select("span#inn");
            Elements addressElement = document.select("div[itemprop = address]");
            Elements directorNameElement = document.select("div[itemtype=http://schema.org/OrganizationRole]").select("a[target=_blank]");
            Elements founderElementNum = document.select("a[data-target=#modal-founders]");
            Elements foundersElement = document.select("table.hidden-print").select("span[itemprop=founder]");
            Elements mainActivityElement = document.select("div[itemprop]:contains(Основной вид деятельности:)");

//            Elements contactsElement = getContactsElements(document);// TODO Нет возможности закончить без покупки премиум аккаунта

            String companyName = getStringFromElement(companyNameElement);
            String status = getStringFromElement(statusElement);
            String date = getDateFromElement(registerDateElement);
            String inn = getStringFromElement(innElement);
            String address = getAddress(addressElement);
            String director = getDirectorsName(directorNameElement, document);
            String mainActivity = getMainActivityFromElement(mainActivityElement);

//            System.out.print(href + " "); //TODO

            int foundersNumber = Integer.parseInt(founderElementNum.first().text());
            List<String> founders = getFounders(document, foundersElement, foundersNumber);
//            System.out.println(director + " ");
//            System.out.println(); //TODO

            company = new Company(inn, director, founders, address, companyName, date, status, mainActivity,"UNKNOWN");
        } catch (Exception e) {
            return;
        }
        COMPANIES.add(company);
    }

    private static Elements getContactsElements(Document document) throws IOException {
        Elements contactsHrefElement = document.select("a:contains(Контакты)");
        String contactsHref = contactsHrefElement.attr("href");
        Document contactsDocument = Jsoup
                .connect(HTTPS_ZACHESTNYIBIZNES_RU + contactsHref)
                .headers(getHeaders())
                .get();

        Elements h2 = contactsDocument.select("h2.f-s-18");
        System.out.println(h2);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Elements contactElement = contactsDocument.select("div.text-left okved-table f-s-14 m-t-20");
        System.out.println(contactElement);
        return null;
    }

    private static String getMainActivityFromElement(Elements mainActivityElement) {
        return mainActivityElement.textNodes().get(3).text().trim();
    }

    private static List<String> getFounders(Document document, Elements foundersElement, int foundersNumber) {
        Set<String> founders = new HashSet<>();
        if (foundersNumber != 0) {

            addElementsToCollection(foundersElement, founders, 1); //Добавление физ лиц

            if (foundersElement.textNodes().isEmpty() || foundersElement.textNodes().size() != foundersNumber) { // Добавление компаний
                foundersElement = document.select("table.hidden-print").select("a[target]");
                addElementsToCollection(foundersElement, founders, 2);
            }

            if (founders.isEmpty() || founders.size() != foundersNumber) { // Добавление гос.Учреждений
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

    private static void addElementsToCollection(Elements foundersElement, Set<String> founders, int step) {
        for (int i = 0; i < foundersElement.textNodes().size(); i = i + step)
            founders.add(foundersElement.get(i).text());
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

    private static Map<String, String> getHeaders() {
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
