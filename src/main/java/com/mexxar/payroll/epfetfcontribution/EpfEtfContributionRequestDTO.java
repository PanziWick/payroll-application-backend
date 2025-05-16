package com.mexxar.payroll.epfetfcontribution;

public record EpfEtfContributionRequestDTO(
        Long paySlipId,

        Double epfContribution,

        Double etfContribution,

        Long employeeId,

        Long payPeriodId
)
{}
