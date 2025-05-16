package com.mexxar.payroll.allowancetype;

import com.mexxar.payroll.salaryallowance.SalaryAllowanceModel;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "AllowanceType")
@Data
public class AllowanceTypeModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private Boolean isFixed;
    private Boolean isLiableToTax;

    @OneToMany
    @JoinColumn(name = "allowance_type_id")
    private List<SalaryAllowanceModel> salaryAllowances;
}
