package com.mexxar.payroll.payslip;

import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class PaySlipSpecification {
    public static Specification<PaySlipModel> paySlipSpecification(
            Long employeeId,
            PaySlipStatusEnum status,
            LocalDate startDate,
            LocalDate endDate,
            Long payPeriodId
    ) {
        return (root, query, cb) -> cb.and(
                employeeId == null ? cb.isTrue(cb.literal(true)) : cb.equal(root.get("employee").get("id"), employeeId),
                status == null ? cb.isTrue(cb.literal(true)) : cb.equal(root.get("status"), status),
                startDate == null ? cb.isTrue(cb.literal(true)) : cb.greaterThanOrEqualTo(root.get("startDate"), startDate),
                endDate == null ? cb.isTrue(cb.literal(true)) : cb.lessThanOrEqualTo(root.get("endDate"), endDate),
                payPeriodId == null ? cb.isTrue(cb.literal(true)) : cb.equal(root.get("salaryPayPeriod").get("id"), payPeriodId)
        );
    }
}
