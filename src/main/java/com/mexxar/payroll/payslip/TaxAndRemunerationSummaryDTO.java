package com.mexxar.payroll.payslip;

public record TaxAndRemunerationSummaryDTO(String monthOf,
                                           Double taxExcludedRemuneration,
                                           Double taxLiableRemuneration,
                                           Double taxDeduction,
                                           Double paymentsMade) {
}
