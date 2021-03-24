package com.borlok.model;

import java.util.List;

public class Company {
    private String inn;
    private String director;
    private List<String> founders;
    private String address;
    private String companyName;
    private String registrationDate;
    private String status;
    private String mainActivity;
    private String tax;

    public Company(String inn, String director, List<String> founders, String address, String companyName, String registrationDate, String status, String mainActivity, String tax) {
        this.inn = inn;
        this.director = director;
        this.founders = founders;
        this.address = address;
        this.companyName = companyName;
        this.registrationDate = registrationDate;
        this.status = status;
        this.mainActivity = mainActivity;
        this.tax = tax;
    }

    public String getInn() {
        return inn;
    }

    public void setInn(String inn) {
        this.inn = inn;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public List<String> getFounders() {
        return founders;
    }

    public void setFounders(List<String> founders) {
        this.founders = founders;
    }

    public String getMainActivity() {
        return mainActivity;
    }

    public void setMainActivity(String mainActivity) {
        this.mainActivity = mainActivity;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTax() {
        return tax;
    }

    public void setTax(String tax) {
        this.tax = tax;
    }

    @Override
    public String toString() {
        return "Company{" +
                "inn='" + inn + '\'' +
                ", director='" + director + '\'' +
                ", founders=" + founders +
                ", address='" + address + '\'' +
                ", companyName='" + companyName + '\'' +
                ", registrationDate='" + registrationDate + '\'' +
                ", status='" + status + '\'' +
                ", mainActivity='" + mainActivity + '\'' +
                ", tax='" + tax + '\'' +
                '}';
    }
}
