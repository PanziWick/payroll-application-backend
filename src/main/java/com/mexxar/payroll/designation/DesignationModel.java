package com.mexxar.payroll.designation;

import com.mexxar.payroll.common.enums.StatusEnum;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "Designation")
@Data
public class DesignationModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String jobTitle;

    private String jobDescription;

    private StatusEnum status;
}
