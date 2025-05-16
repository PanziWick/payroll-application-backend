package com.mexxar.payroll.commissiontype;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "CommissionType")
@Data
public class CommissionTypeModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Boolean isLiableToTax;

    private String description;
}
