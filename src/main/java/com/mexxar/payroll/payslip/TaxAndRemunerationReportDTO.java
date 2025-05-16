package com.mexxar.payroll.payslip;

import java.util.List;

public record TaxAndRemunerationReportDTO(Long year,
                                          List<TaxAndRemunerationSummaryDTO> taxAndRemunerationSummaryDTOList,
                                          Double totalTaxExcludedRemuneration,
                                          Double totalTaxLiableRemuneration,
                                          Double totalTaxDeduction,
                                          Double totalPaymentMade) {
}
