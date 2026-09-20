package com.example.attendance_tracker_be.controller;

import com.example.attendance_tracker_be.dto.ListedCompanyResponse;
import com.example.attendance_tracker_be.model.ListedCompany;
import com.example.attendance_tracker_be.service.ListedCompanyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/companies")
public class ListedCompanyController {

    private final ListedCompanyService listedCompanyService;

    public ListedCompanyController(ListedCompanyService listedCompanyService) {
        this.listedCompanyService = listedCompanyService;
    }

    @GetMapping
    public ResponseEntity<List<ListedCompany>> getAllListedCompanies() {
        return ResponseEntity.ok(listedCompanyService.getAllListedCompanies());
    }
}