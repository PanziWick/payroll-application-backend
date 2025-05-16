package com.mexxar.payroll.department;

import com.mexxar.payroll.common.enums.StatusEnum;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "Department")
@Data
public class DepartmentModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private StatusEnum status;
}
