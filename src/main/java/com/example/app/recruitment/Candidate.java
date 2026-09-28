package com.example.app.recruitment;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "candidate")
public class Candidate {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String email;

  @Column(nullable = false)
  private String resumeLink;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private CandidateStage stage;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  private JobOpening jobOpening;

  public Long getId() { return id; }
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
  public String getEmail() { return email; }
  public void setEmail(String email) { this.email = email; }
  public String getResumeLink() { return resumeLink; }
  public void setResumeLink(String resumeLink) { this.resumeLink = resumeLink; }
  public CandidateStage getStage() { return stage; }
  public void setStage(CandidateStage stage) { this.stage = stage; }
  public JobOpening getJobOpening() { return jobOpening; }
  public void setJobOpening(JobOpening jobOpening) { this.jobOpening = jobOpening; }
}
