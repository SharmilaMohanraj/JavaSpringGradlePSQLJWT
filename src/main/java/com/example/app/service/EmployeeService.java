package com.example.app.service;

import com.example.app.entity.Employee;
import com.example.app.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

@Service
public class EmployeeService {
  private final EmployeeRepository employeeRepository;

  public EmployeeService(EmployeeRepository employeeRepository) {
    this.employeeRepository = employeeRepository;
  }

  public Employee provisionEmployee(String name, String email) {
    Employee employee = new Employee();
    employee.setName(name);
    employee.setEmail(email);
    return employeeRepository.save(employee);
  }
}
