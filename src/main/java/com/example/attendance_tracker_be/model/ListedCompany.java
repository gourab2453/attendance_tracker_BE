package com.example.attendance_tracker_be.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document(collection = "listedCompany")
public class ListedCompany {

    @Id
    private String id;

    private String companyId;
    private String companyName;
    private LocalDate foundationDate;
    private String ceo;

    public ListedCompany() {}

    public ListedCompany(String companyId, String companyName, LocalDate foundationDate, String ceo) {
        this.companyId = companyId;
        this.companyName = companyName;
        this.foundationDate = foundationDate;
        this.ceo = ceo;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCompanyId() { return companyId; }
    public void setCompanyId(String companyId) { this.companyId = companyId; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public LocalDate getFoundationDate() { return foundationDate; }
    public void setFoundationDate(LocalDate foundationDate) { this.foundationDate = foundationDate; }

    public String getCeo() { return ceo; }
    public void setCeo(String ceo) { this.ceo = ceo; }
}