package com.example.app.controller;

import com.example.app.entity.Department;
import com.example.app.entity.Designation;
import com.example.app.entity.Employee;
import com.example.app.entity.LeaveRequest;
import com.example.app.entity.ManagerSubordinateHierarchy;
import com.example.app.entity.PayrollRecord;
import com.example.app.entity.PerformanceReviewCycle;
import com.example.app.repository.DepartmentRepository;
import com.example.app.repository.DesignationRepository;
import com.example.app.repository.EmployeeRepository;
import com.example.app.repository.LeaveRequestRepository;
import com.example.app.repository.ManagerSubordinateHierarchyRepository;
import com.example.app.repository.PayrollRecordRepository;
import com.example.app.repository.PerformanceReviewCycleRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Human Resources")
public class HrController {
  private final DepartmentRepository departments;
  private final DesignationRepository designations;
  private final EmployeeRepository employees;
  private final LeaveRequestRepository leaves;
  private final PayrollRecordRepository payrolls;
  private final PerformanceReviewCycleRepository reviews;
  private final ManagerSubordinateHierarchyRepository hierarchies;

  public HrController(
      DepartmentRepository departments,
      DesignationRepository designations,
      EmployeeRepository employees,
      LeaveRequestRepository leaves,
      PayrollRecordRepository payrolls,
      PerformanceReviewCycleRepository reviews,
      ManagerSubordinateHierarchyRepository hierarchies) {
    this.departments = departments;
    this.designations = designations;
    this.employees = employees;
    this.leaves = leaves;
    this.payrolls = payrolls;
    this.reviews = reviews;
    this.hierarchies = hierarchies;
  }

  @PostMapping("/{type}")
  @Operation(summary = "Create a human resources record")
  public ResponseEntity<?> create(
      @PathVariable String type, @RequestBody Map<String, Object> body) {
    try {
      return ResponseEntity.ok(view(createRecord(type, body)));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  @GetMapping("/{type}")
  @Operation(summary = "List human resources records")
  public ResponseEntity<?> list(@PathVariable String type) {
    try {
      return ResponseEntity.ok(all(type).stream().map(this::view).collect(Collectors.toList()));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  @GetMapping("/{type}/{id}")
  @Operation(summary = "Get a human resources record")
  public ResponseEntity<?> get(@PathVariable String type, @PathVariable Long id) {
    Object value = find(type, id);
    return value == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(view(value));
  }

  @PutMapping("/{type}/{id}")
  @Operation(summary = "Update a human resources record")
  public ResponseEntity<?> update(
      @PathVariable String type, @PathVariable Long id, @RequestBody Map<String, Object> body) {
    if (find(type, id) == null) return ResponseEntity.notFound().build();
    body.put("id", id);
    try {
      return ResponseEntity.ok(view(createRecord(type, body)));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  @DeleteMapping("/{type}/{id}")
  @Operation(summary = "Delete a human resources record")
  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
  public ResponseEntity<?> delete(@PathVariable String type, @PathVariable Long id) {
    if (find(type, id) == null) return ResponseEntity.notFound().build();
    remove(type, id);
    return ResponseEntity.noContent().build();
  }

  @PutMapping("/leave-requests/{id}/status")
  @Operation(summary = "Approve or reject a leave request")
  @PreAuthorize("hasAnyAuthority('ROLE_MANAGER', 'ROLE_ADMIN')")
  public ResponseEntity<?> status(@PathVariable Long id, @RequestBody Map<String, String> body) {
    LeaveRequest leave = leaves.findById(id).orElse(null);
    if (leave == null) return ResponseEntity.notFound().build();
    try {
      LeaveRequest.Status status = LeaveRequest.Status.valueOf(body.get("status"));
      if (status == LeaveRequest.Status.PENDING)
        return ResponseEntity.badRequest().body("Status must be APPROVED or REJECTED");
      if (status == LeaveRequest.Status.APPROVED
          && leave.getDays() > leave.getEmployee().getLeaveBalance())
        return ResponseEntity.badRequest().body("Insufficient leave balance");
      leave.setStatus(status);
      if (status == LeaveRequest.Status.APPROVED)
        leave
            .getEmployee()
            .setLeaveBalance(leave.getEmployee().getLeaveBalance() - leave.getDays());
      return ResponseEntity.ok(view(leaves.save(leave)));
    } catch (Exception e) {
      return ResponseEntity.badRequest().body("Invalid status");
    }
  }

  @GetMapping("/reports/leave-balances")
  @Operation(summary = "Report employee leave balances")
  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
  public List<Map<String, Object>> balances() {
    return employees.findAll().stream().map(this::view).collect(Collectors.toList());
  }

  private Object createRecord(String type, Map<String, Object> b) {
    Long id = b.get("id") == null ? null : Long.valueOf(b.get("id").toString());
    if ("departments".equals(type)) {
      Department x = id == null ? new Department() : departments.findById(id).orElseThrow();
      x.setName(required(b, "name"));
      return departments.save(x);
    }
    if ("designations".equals(type)) {
      Designation x = id == null ? new Designation() : designations.findById(id).orElseThrow();
      x.setTitle(required(b, "title"));
      return designations.save(x);
    }
    if ("employees".equals(type)) {
      Employee x = id == null ? new Employee() : employees.findById(id).orElseThrow();
      x.setName(required(b, "name"));
      x.setEmail(required(b, "email"));
      x.setLeaveBalance(
          b.get("leaveBalance") == null
              ? x.getLeaveBalance()
              : Integer.valueOf(b.get("leaveBalance").toString()));
      if (b.get("departmentId") != null)
        x.setDepartment(
            departments.findById(Long.valueOf(b.get("departmentId").toString())).orElseThrow());
      if (b.get("designationId") != null)
        x.setDesignation(
            designations.findById(Long.valueOf(b.get("designationId").toString())).orElseThrow());
      return employees.save(x);
    }
    if ("leave-requests".equals(type)) {
      LeaveRequest x = id == null ? new LeaveRequest() : leaves.findById(id).orElseThrow();
      x.setEmployee(employees.findById(Long.valueOf(required(b, "employeeId"))).orElseThrow());
      x.setReason(required(b, "reason"));
      x.setDays(Integer.valueOf(required(b, "days")));
      if (x.getDays() < 1) throw new IllegalArgumentException("days must be positive");
      return leaves.save(x);
    }
    if ("payroll-records".equals(type)) {
      PayrollRecord x = id == null ? new PayrollRecord() : payrolls.findById(id).orElseThrow();
      x.setEmployee(employees.findById(Long.valueOf(required(b, "employeeId"))).orElseThrow());
      x.setAmount(new BigDecimal(required(b, "amount")));
      x.setPeriod(required(b, "period"));
      return payrolls.save(x);
    }
    if ("performance-reviews".equals(type)) {
      PerformanceReviewCycle x =
          id == null ? new PerformanceReviewCycle() : reviews.findById(id).orElseThrow();
      x.setEmployee(employees.findById(Long.valueOf(required(b, "employeeId"))).orElseThrow());
      x.setCycleName(required(b, "cycleName"));
      x.setFeedback(required(b, "feedback"));
      return reviews.save(x);
    }
    if ("hierarchies".equals(type)) {
      ManagerSubordinateHierarchy x =
          id == null ? new ManagerSubordinateHierarchy() : hierarchies.findById(id).orElseThrow();
      x.setManager(employees.findById(Long.valueOf(required(b, "managerId"))).orElseThrow());
      x.setSubordinate(
          employees.findById(Long.valueOf(required(b, "subordinateId"))).orElseThrow());
      return hierarchies.save(x);
    }
    throw new IllegalArgumentException("Unknown record type");
  }

  private String required(Map<String, Object> b, String key) {
    if (b.get(key) == null || b.get(key).toString().trim().isEmpty())
      throw new IllegalArgumentException(key + " is required");
    return b.get(key).toString();
  }

  private List<?> all(String type) {
    if ("departments".equals(type)) return departments.findAll();
    if ("designations".equals(type)) return designations.findAll();
    if ("employees".equals(type)) return employees.findAll();
    if ("leave-requests".equals(type)) return leaves.findAll();
    if ("payroll-records".equals(type)) return payrolls.findAll();
    if ("performance-reviews".equals(type)) return reviews.findAll();
    if ("hierarchies".equals(type)) return hierarchies.findAll();
    throw new IllegalArgumentException("Unknown record type");
  }

  private Object find(String type, Long id) {
    if ("departments".equals(type)) return departments.findById(id).orElse(null);
    if ("designations".equals(type)) return designations.findById(id).orElse(null);
    if ("employees".equals(type)) return employees.findById(id).orElse(null);
    if ("leave-requests".equals(type)) return leaves.findById(id).orElse(null);
    if ("payroll-records".equals(type)) return payrolls.findById(id).orElse(null);
    if ("performance-reviews".equals(type)) return reviews.findById(id).orElse(null);
    if ("hierarchies".equals(type)) return hierarchies.findById(id).orElse(null);
    throw new IllegalArgumentException("Unknown record type");
  }

  private void remove(String type, Long id) {
    if ("departments".equals(type)) departments.deleteById(id);
    else if ("designations".equals(type)) designations.deleteById(id);
    else if ("employees".equals(type)) employees.deleteById(id);
    else if ("leave-requests".equals(type)) leaves.deleteById(id);
    else if ("payroll-records".equals(type)) payrolls.deleteById(id);
    else if ("performance-reviews".equals(type)) reviews.deleteById(id);
    else if ("hierarchies".equals(type)) hierarchies.deleteById(id);
    else throw new IllegalArgumentException("Unknown record type");
  }

  private Map<String, Object> view(Object o) {
    if (o instanceof Department) {
      Department x = (Department) o;
      return Map.of("id", x.getId(), "name", x.getName());
    }
    if (o instanceof Designation) {
      Designation x = (Designation) o;
      return Map.of("id", x.getId(), "title", x.getTitle());
    }
    if (o instanceof Employee) {
      Employee x = (Employee) o;
      return Map.of(
          "id",
          x.getId(),
          "name",
          x.getName(),
          "email",
          x.getEmail(),
          "leaveBalance",
          x.getLeaveBalance());
    }
    if (o instanceof LeaveRequest) {
      LeaveRequest x = (LeaveRequest) o;
      return Map.of(
          "id",
          x.getId(),
          "reason",
          x.getReason(),
          "days",
          x.getDays(),
          "status",
          x.getStatus().name(),
          "employeeId",
          x.getEmployee().getId());
    }
    if (o instanceof PayrollRecord) {
      PayrollRecord x = (PayrollRecord) o;
      return Map.of(
          "id",
          x.getId(),
          "amount",
          x.getAmount(),
          "period",
          x.getPeriod(),
          "employeeId",
          x.getEmployee().getId());
    }
    if (o instanceof PerformanceReviewCycle) {
      PerformanceReviewCycle x = (PerformanceReviewCycle) o;
      return Map.of(
          "id",
          x.getId(),
          "cycleName",
          x.getCycleName(),
          "feedback",
          x.getFeedback(),
          "employeeId",
          x.getEmployee().getId());
    }
    ManagerSubordinateHierarchy x = (ManagerSubordinateHierarchy) o;
    return Map.of(
        "id",
        x.getId(),
        "managerId",
        x.getManager().getId(),
        "subordinateId",
        x.getSubordinate().getId());
  }
}
