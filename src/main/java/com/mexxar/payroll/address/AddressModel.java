package com.mexxar.payroll.address;

import com.mexxar.payroll.address.enums.TypeEnum;
import com.mexxar.payroll.employee.EmployeeModel;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "Address")
@Data
public class AddressModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private TypeEnum type;

    private String addressLine1;

    private String addressLine2;

    private String city;

    private String postalCode;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private EmployeeModel employee;
}
