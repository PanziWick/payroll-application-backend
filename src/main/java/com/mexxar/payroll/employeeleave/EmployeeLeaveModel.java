package com.mexxar.payroll.employeeleave;

import com.mexxar.payroll.employee.EmployeeModel;
import com.mexxar.payroll.leave.LeavePolicyModel;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "EmployeeLeave")
@Data
public class EmployeeLeaveModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate startDate;
    private LocalDate endDate;
    private Long year;

    private Double numberOfDays;
    private EmployeeLeaveEnum status;
    private String approvedBy;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private EmployeeModel employee;

    @ManyToOne
    @JoinColumn(name = "leave_policy_id")
    private LeavePolicyModel leavePolicy;
}
