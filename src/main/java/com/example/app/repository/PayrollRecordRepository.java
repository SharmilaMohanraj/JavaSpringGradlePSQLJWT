package com.example.app.repository;

import com.example.app.entity.PayrollRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PayrollRecordRepository extends JpaRepository<PayrollRecord, Long> {}
