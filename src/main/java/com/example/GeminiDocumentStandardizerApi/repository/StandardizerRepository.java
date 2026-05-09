package com.example.GeminiDocumentStandardizerApi.repository;

import com.example.GeminiDocumentStandardizerApi.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StandardizerRepository extends JpaRepository<Report, Long> {
}
