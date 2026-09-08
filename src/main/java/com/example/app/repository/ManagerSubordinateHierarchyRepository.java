package com.example.app.repository;

import com.example.app.entity.ManagerSubordinateHierarchy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ManagerSubordinateHierarchyRepository
    extends JpaRepository<ManagerSubordinateHierarchy, Long> {}
