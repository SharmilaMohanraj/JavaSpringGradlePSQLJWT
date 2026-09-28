package com.example.app.recruitment;

/** Signals that a recruitment resource or referenced HR resource does not exist. */
public class RecruitmentNotFoundException extends RuntimeException {
  public RecruitmentNotFoundException(String message) {
    super(message);
  }
}
