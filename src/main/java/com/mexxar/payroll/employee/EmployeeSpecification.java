package com.mexxar.payroll.employee;

import com.mexxar.payroll.common.enums.StatusEnum;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class EmployeeSpecification {
    public static Specification<EmployeeModel> employeeSpecification(
            String contactNumber,
            LocalDate hireFrom,
            LocalDate hireTo,
            StatusEnum status,
            Long departmentId,
            Long designationId,
            String searchQuery

    ) {
        return (root, query, cb) -> cb.and(
                contactNumber == null ? cb.isTrue(cb.literal(true)) : cb.like(root.get("contactNumber"), "%" + contactNumber + "%"),
                hireFrom == null ? cb.isTrue(cb.literal(true)) : cb.greaterThanOrEqualTo(root.get("hireDate"), hireFrom),
                hireTo == null ? cb.isTrue(cb.literal(true)) : cb.lessThanOrEqualTo(root.get("hireDate"), hireTo),
                status == null ? cb.isTrue(cb.literal(true)) : cb.equal(root.get("status"),status),
                departmentId == null ? cb.isTrue(cb.literal(true)) : cb.equal(root.join("department").get("id"), departmentId),
                designationId == null ? cb.isTrue(cb.literal(true)) : cb.equal(root.get("designation").get("id"), designationId),

                searchQuery == null ? cb.isTrue(cb.literal(true)) : cb.or(
                        cb.like(root.get("firstName"), "%" + searchQuery + "%"),
                        cb.like(root.get("lastName"), "%" + searchQuery + "%"),
                        cb.like(root.get("email"), "%" + searchQuery + "%")
                )
        );
    }
}
