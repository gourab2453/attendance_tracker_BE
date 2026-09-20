package com.example.attendance_tracker_be.dto;

import java.time.LocalDate;

public class ListedCompanyResponse {

    private String companyId;
    private String companyName;
    private LocalDate foundationDate;
    private String ceo;

    public ListedCompanyResponse(String companyId, String companyName, LocalDate foundationDate, String ceo) {
        this.companyId = companyId;
        this.companyName = companyName;
        this.foundationDate = foundationDate;
        this.ceo = ceo;
    }

    public String getCompanyId() { return companyId; }
    public String getCompanyName() { return companyName; }
    public LocalDate getFoundationDate() { return foundationDate; }
    public String getCeo() { return ceo; }
}