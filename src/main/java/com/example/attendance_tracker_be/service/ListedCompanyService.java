package com.example.attendance_tracker_be.service;

import com.example.attendance_tracker_be.dto.ListedCompanyResponse;
import com.example.attendance_tracker_be.model.ListedCompany;
import com.example.attendance_tracker_be.repository.ListedCompanyRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListedCompanyService {

    private final ListedCompanyRepository listedCompanyRepository;

    public ListedCompanyService(ListedCompanyRepository listedCompanyRepository) {
        this.listedCompanyRepository = listedCompanyRepository;
    }

    public List<ListedCompany> getAllListedCompanies() {
        return listedCompanyRepository.findAll();
    }
}