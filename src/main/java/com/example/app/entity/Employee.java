package com.example.app.entity;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class Employee {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;
  private String email;
  private Integer leaveBalance = 20;

  @ManyToOne(fetch = FetchType.LAZY)
  private Department department;

  @ManyToOne(fetch = FetchType.LAZY)
  private Designation designation;

  @OneToMany(mappedBy = "employee")
  private List<LeaveRequest> leaveRequests = new ArrayList<>();

  @OneToMany(mappedBy = "employee")
  private List<PayrollRecord> payrollRecords = new ArrayList<>();

  @OneToMany(mappedBy = "employee")
  private List<PerformanceReviewCycle> performanceReviews = new ArrayList<>();

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public Integer getLeaveBalance() {
    return leaveBalance;
  }

  public void setLeaveBalance(Integer leaveBalance) {
    this.leaveBalance = leaveBalance;
  }

  public Department getDepartment() {
    return department;
  }

  public void setDepartment(Department department) {
    this.department = department;
  }

  public Designation getDesignation() {
    return designation;
  }

  public void setDesignation(Designation designation) {
    this.designation = designation;
  }
}
