package com.example.app.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class ManagerSubordinateHierarchy {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  private Employee manager;

  @ManyToOne(fetch = FetchType.LAZY)
  private Employee subordinate;

  public Long getId() {
    return id;
  }

  public Employee getManager() {
    return manager;
  }

  public void setManager(Employee manager) {
    this.manager = manager;
  }

  public Employee getSubordinate() {
    return subordinate;
  }

  public void setSubordinate(Employee subordinate) {
    this.subordinate = subordinate;
  }
}
