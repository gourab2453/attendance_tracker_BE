package com.example.attendance_tracker_be.repository;

import com.example.attendance_tracker_be.model.ListedCompany;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ListedCompanyRepository extends MongoRepository<ListedCompany, String> {
    boolean existsByCompanyName(String companyName);
}