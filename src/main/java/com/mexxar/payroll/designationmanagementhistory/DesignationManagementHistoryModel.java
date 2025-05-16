package com.mexxar.payroll.designationmanagementhistory;

import com.mexxar.payroll.employee.EmployeeModel;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Table(name = "DesignationManagementHistory")
@Data
public class DesignationManagementHistoryModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private EmployeeModel employee;

    private Long previousDesignationId;
    private Long currentDesignationId;

    private Long previousDepartmentId;
    private Long currentDepartmentId;

    @Temporal(TemporalType.TIMESTAMP)
    private Date changeDate;

    @Column(name = "changed_by", nullable = false)
    private String changedBy;
}
