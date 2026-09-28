package com.example.app.recruitment;

import com.example.app.entity.Department;
import com.example.app.entity.Designation;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "job_opening")
public class JobOpening {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false)
  private String description;

  @Column(nullable = false)
  private Integer vacancyCount;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  private Department department;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  private Designation designation;

  @OneToMany(mappedBy = "jobOpening")
  private List<Candidate> candidates = new ArrayList<>();

  public Long getId() { return id; }
  public String getTitle() { return title; }
  public void setTitle(String title) { this.title = title; }
  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }
  public Integer getVacancyCount() { return vacancyCount; }
  public void setVacancyCount(Integer vacancyCount) { this.vacancyCount = vacancyCount; }
  public Department getDepartment() { return department; }
  public void setDepartment(Department department) { this.department = department; }
  public Designation getDesignation() { return designation; }
  public void setDesignation(Designation designation) { this.designation = designation; }
  public List<Candidate> getCandidates() { return candidates; }
  public void setCandidates(List<Candidate> candidates) { this.candidates = candidates; }
}
