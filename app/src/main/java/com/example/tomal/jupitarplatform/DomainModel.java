package com.example.tomal.jupitarplatform;

import java.util.Comparator;

public class DomainModel {
    private int site_id;
    private String domain_name;
    private int company_id;
    private String company_name;
    private String contact;

    public int getSite_id() {
        return site_id;
    }

    public void setSite_id(int site_id) {
        this.site_id = site_id;
    }

    public String getDomain_name() {
        return domain_name;
    }

    public void setDomain_name(String domain_name) {
        this.domain_name = domain_name;
    }

    public int getCompany_id() {
        return company_id;
    }

    public void setCompany_id(int company_id) {
        this.company_id = company_id;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public DomainModel(int site_id, String domain_name, int company_id, String company_name, String contact) {

        this.site_id = site_id;
        this.domain_name = domain_name;
        this.company_id = company_id;
        this.company_name = company_name;
        this.contact = contact;
    }

    public DomainModel() {

    }

    @Override
    public String toString() {
        return domain_name;
    }
    public static Comparator<DomainModel> checkDomainNameComparator = new Comparator<DomainModel>() {

        public int compare(DomainModel s1, DomainModel s2) {
            String StudentName1 = s1.getDomain_name().toUpperCase();
            String StudentName2 = s2.getDomain_name().toUpperCase();

            //ascending order
            return StudentName1.compareTo(StudentName2);

            //descending order
            //return StudentName2.compareTo(StudentName1);
        }};
}
