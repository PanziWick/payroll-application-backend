package com.mexxar.payroll.employee;

import com.mexxar.payroll.common.enums.StatusEnum;
import com.mexxar.payroll.department.DepartmentModel;
import com.mexxar.payroll.designation.DesignationModel;
import com.mexxar.payroll.employee.enums.GenderEnum;
import com.mexxar.payroll.employee.enums.MaritalEnum;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "Employee")
@Data
public class EmployeeModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;

    private String middleName;

    private String lastName;

    private String email;

    private LocalDate dob;

    private String contactNumber;

    private LocalDate hireDate;

    private String epfNumber;

    private String nationalIdNumber;

    private GenderEnum gender;

    private MaritalEnum marital;

    @ManyToOne
    @JoinColumn(name = "department_id", nullable = false)
    private DepartmentModel department;

    @ManyToOne
    @JoinColumn(name = "designation_id", nullable = false)
    private DesignationModel designation;

    private StatusEnum status;
}
