package com.mexxar.payroll.tax;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "Tax")
@Data
public class TaxModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double taxRate;
    private Double minSalary;
    private Double maxSalary;
}
