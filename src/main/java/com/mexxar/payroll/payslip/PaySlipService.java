package com.mexxar.payroll.payslip;

import com.mexxar.payroll.common.ApiResponseDTO;
import com.mexxar.payroll.employeeleave.EmployeeLeaveService;
import com.mexxar.payroll.epfetfcontribution.EpfEtfContributionRequestDTO;
import com.mexxar.payroll.epfetfcontribution.EpfEtfContributionService;
import com.mexxar.payroll.loan.LoanResponseDTO;
import com.mexxar.payroll.loan.LoanService;
import com.mexxar.payroll.payslip.exception.PaySlipNotFoundException;
import com.mexxar.payroll.payslipdetails.*;
import com.mexxar.payroll.salary.SalaryResponseDTO;
import com.mexxar.payroll.salary.SalaryService;
import com.mexxar.payroll.salaryadvance.SalaryAdvanceResponseDTO;
import com.mexxar.payroll.salaryadvance.SalaryAdvanceService;
import com.mexxar.payroll.salaryallowance.SalaryAllowanceModel;
import com.mexxar.payroll.salaryallowance.SalaryAllowanceService;
import com.mexxar.payroll.salarycommission.SalaryCommissionResponseDTO;
import com.mexxar.payroll.salarycommission.SalaryCommissionService;
import com.mexxar.payroll.salarypayperiod.SalaryPayPeriodModel;
import com.mexxar.payroll.salarypayperiod.SalaryPayPeriodResponseDTO;
import com.mexxar.payroll.salarypayperiod.SalaryPayPeriodService;
import com.mexxar.payroll.tax.TaxResponseDTO;
import com.mexxar.payroll.tax.TaxService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaySlipService {

    private final PaySlipRepository paySlipRepository;
    private final PaySlipDetailsRepository paySlipDetailsRepository;
    private final SalaryService salaryService;
    private final PaySlipDetailsService paySlipDetailsService;
    private final SalaryAllowanceService salaryAllowanceService;
    private final SalaryCommissionService salaryCommissionService;
    private final SalaryAdvanceService salaryAdvanceService;
    private final LoanService loanService;
    private final TaxService taxService;
    private final EpfEtfContributionService epfEtfContributionService;
    private final SalaryPayPeriodService salaryPayPeriodService;
    private final EmployeeLeaveService employeeLeaveService; //inject service instead

    private static final Logger logger = LoggerFactory.getLogger(PaySlipService.class);

    private static final String PAYSLIP_NOT_FOUND_WITH_ID = "PaySlip not found with ID: ";

    @Transactional
    public ApiResponseDTO<PaySlipResponseDTO> createPaySlip(PaySlipRequestDTO requestDTO) {
        SalaryResponseDTO salary = salaryService.getSalaryByEmployeeId(requestDTO.employeeId());

        // Calculate the employee basic salary based on joined date
        double basicSalary = getBasicSalary(requestDTO, salary);

        // Calculate working days between the period start and end dates
        int workingDays = calculateWorkingDays(requestDTO.startDate(), requestDTO.endDate());

        // Daily salary is based on basic salary divided by working days
        double dailySalary = basicSalary / workingDays;

        // Fetch the total number of no-pay leave days in the period for the employee
        Double totalNoPayLeaveDays = employeeLeaveService.getTotalNoPayLeaveDays(
                salary.employee().getId(),
                requestDTO.startDate(),
                requestDTO.endDate()
        );

        // Calculate leave deduction as number of no-pay leave days multiplied by daily salary
        double leaveDeduction = totalNoPayLeaveDays * dailySalary;

        // region Process tax liable salary allowances

        // Fixed Allowances
        List<SalaryAllowanceModel> taxLiableFixedAllowances =
                salaryAllowanceService.getAllowancesByCriteria(salary.id(), 0, true, true);

        double taxLiableFixedAllowanceAmount = taxLiableFixedAllowances.stream()
                .mapToDouble(SalaryAllowanceModel::getAmount)
                .sum();

        List<SalaryAllowanceModel> allSalaryAllowanceList = new ArrayList<>(taxLiableFixedAllowances);

        // Monthly Allowances
        List<SalaryAllowanceModel> taxLiableMonthlyAllowances =
                salaryAllowanceService.getAllowancesByCriteria(salary.id(), requestDTO.payPeriodId(), false, true);

        double taxLiableMonthlyAllowanceAmount = taxLiableMonthlyAllowances.stream()
                .mapToDouble(SalaryAllowanceModel::getAmount)
                .sum();

        allSalaryAllowanceList.addAll(taxLiableMonthlyAllowances);

        double totalTaxLiableSalaryAllowances = taxLiableFixedAllowanceAmount + taxLiableMonthlyAllowanceAmount;

        // endregion

        // region Process tax excluded salary allowances

        // Tax excluded fixed allowance
        List<SalaryAllowanceModel> taxExcludedFixedAllowances =
                salaryAllowanceService.getAllowancesByCriteria(salary.id(), 0, true, false);

        double taxExcludedFixedAllowanceAmount = taxExcludedFixedAllowances.stream()
                .mapToDouble(SalaryAllowanceModel::getAmount)
                .sum();

        allSalaryAllowanceList.addAll(taxExcludedFixedAllowances);


        // Tax excluded monthly allowances
        List<SalaryAllowanceModel> taxExcludedMonthlyAllowances =
                salaryAllowanceService.getAllowancesByCriteria(salary.id(), requestDTO.payPeriodId(), false, false);
        double taxExcludedMonthlyAllowanceAmount = taxExcludedMonthlyAllowances.stream()
                .mapToDouble(SalaryAllowanceModel::getAmount)
                .sum();

        allSalaryAllowanceList.addAll(taxExcludedMonthlyAllowances);

        double totalTaxExcludedSalaryAllowances = taxExcludedFixedAllowanceAmount + taxExcludedMonthlyAllowanceAmount;

        // endregion

        // region Process tax liable salary commissions
        List<SalaryCommissionResponseDTO> taxLiableSalaryCommissions =
                salaryCommissionService.getAllSalaryCommissionByCriteria(salary.id(), requestDTO.payPeriodId(), true);

        double totalTaxLiableSalaryCommission = taxLiableSalaryCommissions.stream()
                .mapToDouble(SalaryCommissionResponseDTO::amount)
                .sum();

        List<SalaryCommissionResponseDTO> salaryCommissionsList = new ArrayList<>(taxLiableSalaryCommissions);
        // endregion

        // region Process tax excluded salary commissions
        List<SalaryCommissionResponseDTO> taxExcludedSalaryCommissions =
                salaryCommissionService.getAllSalaryCommissionByCriteria(salary.id(), requestDTO.payPeriodId(), false);

        double taxExcludedSalaryCommissionAmount = taxExcludedSalaryCommissions.stream()
                .mapToDouble(SalaryCommissionResponseDTO::amount)
                .sum();

        salaryCommissionsList.addAll(taxExcludedSalaryCommissions);
        // endregion

        // Calculate gross salary
        double grossSalary = salary.basicSalary() + totalTaxLiableSalaryAllowances + totalTaxLiableSalaryCommission;

        // Calculate total tax deductions
        double totalTax = calculateTax(grossSalary);

        // region Process salary advances
        List<SalaryAdvanceResponseDTO> salaryAdvances =
                salaryAdvanceService.getPendingSalaryAdvancesByMonthAndEmployeeId(requestDTO.employeeId(), requestDTO.payPeriodId());
        double totalAdvances = salaryAdvances.stream()
                .mapToDouble(SalaryAdvanceResponseDTO::advanceAmount)
                .sum();
        // endregion

        // region Process loan deductions
        List<LoanResponseDTO> loans =
                loanService.getOngoingLoansByEmployeeId(requestDTO.employeeId());
        double totalLoanDeductions = 0;
        for (LoanResponseDTO loan : loans) {
            totalLoanDeductions += getMonthlyLoanInstallmentAmount(loan.loanAmount(), loan.monthlyInstallments(), loan.interestRate());
        }

        // endregion

        // region Process EPF & ETF contributions
        // Calculate EPF deduction (8% of basic salary)
        double epfDeduction = basicSalary * 0.08;

        // Calculate company contributions
        double companyEpf = basicSalary * 0.12;
        double companyEtf = basicSalary * 0.03;

        // endregion

        double netSalary = (grossSalary + totalTaxExcludedSalaryAllowances + taxExcludedSalaryCommissionAmount)
                - (totalTax + totalAdvances + totalLoanDeductions + epfDeduction + requestDTO.attendanceDeduction() + leaveDeduction);

        SalaryPayPeriodModel payPeriod = salaryPayPeriodService.getPayPeriodModelById(requestDTO.payPeriodId());

        PaySlipModel paySlip = new PaySlipModel();
        paySlip.setSalaryId(salary.id());
        paySlip.setBasicSalary(basicSalary);
        paySlip.setAllowances(totalTaxExcludedSalaryAllowances + totalTaxLiableSalaryAllowances);
        paySlip.setCommission(totalTaxLiableSalaryCommission + taxExcludedSalaryCommissionAmount);
        paySlip.setStatus(requestDTO.status());
        paySlip.setGrossSalary(grossSalary);
        paySlip.setSalaryAdvanceDeduction(totalAdvances);
        paySlip.setLoanDeduction(totalLoanDeductions);
        paySlip.setAttendanceDeduction(requestDTO.attendanceDeduction());
        paySlip.setTaxDeduction(totalTax);
        paySlip.setEpfDeduction(epfDeduction);
        paySlip.setStartDate(requestDTO.startDate());
        paySlip.setEndDate(requestDTO.endDate());
        paySlip.setEmployee(salary.employee());
        paySlip.setNetSalary(netSalary);
        paySlip.setSalaryPayPeriod(payPeriod);
        paySlip.setTaxExcludedAllowances(totalTaxExcludedSalaryAllowances);
        paySlip.setTaxLiableAllowances(totalTaxLiableSalaryAllowances);
        paySlip.setTaxExcludedCommissions(taxExcludedSalaryCommissionAmount);
        paySlip.setTaxLiableCommissions(totalTaxLiableSalaryCommission);
        paySlip.setLeaveDeduction(leaveDeduction);

        PaySlipModel savedPaySlip = paySlipRepository.save(paySlip);
        logger.info("PaySlip created with ID: {}", savedPaySlip.getId());

        // Persist the EPF-ETF Contributions
        saveEpfEtfContribution(requestDTO.employeeId(), companyEpf, companyEtf, requestDTO.payPeriodId(), savedPaySlip.getId());

        // Persist the tax details
        saveTax(totalTax, savedPaySlip.getId());

        // Persist the allowances details
        saveAllowances(allSalaryAllowanceList, savedPaySlip.getId());

        // Persist the commission details
        saveCommissions(salaryCommissionsList, savedPaySlip.getId());

        // Persist the advance deductions
        saveAdvances(salaryAdvances, savedPaySlip.getId());

        // Persist the loan deductions
        saveLoans(loans, savedPaySlip.getId());

        // Persist the EPF detail in the payslip details
        saveEPF(epfDeduction, savedPaySlip.getId());

        logger.info("Completed creation of PaySlip with ID: {}", savedPaySlip.getId());

        return new ApiResponseDTO<>("PaySlip Created Successfully", convertToResponseDTO(savedPaySlip));
    }

    // Helper method to calculate working days (excluding weekends)
    private int calculateWorkingDays(LocalDate start, LocalDate end) {
        int workingDays = 0;
        LocalDate date = start;
        while (!date.isAfter(end)) {
            if (date.getDayOfWeek() != DayOfWeek.SATURDAY &&
                    date.getDayOfWeek() != DayOfWeek.SUNDAY) {
                workingDays++;
            }
            date = date.plusDays(1);
        }

        // Ensure workingDays is not zero
        if (workingDays == 0) {
            throw new IllegalArgumentException("Working days cannot be zero. Please check the start and end dates.");
        }

        return workingDays;
    }

    private static double getBasicSalary(PaySlipRequestDTO requestDTO, SalaryResponseDTO salary) {
        LocalDate hireDate = salary.employee().getHireDate();

        // Calculate the basic salary considering if the employee is newly hired
        double basicSalary = salary.basicSalary();
        if (hireDate.isAfter(requestDTO.startDate()) && hireDate.isBefore(requestDTO.endDate())) {
            // Pro-rate salary for new employees
            long totalDaysInPeriod = requestDTO.startDate().until(requestDTO.endDate()).getDays() + 1L;
            long workingDays = hireDate.until(requestDTO.endDate()).getDays() + 1L;

            basicSalary = (basicSalary / totalDaysInPeriod) * workingDays;
        }
        return basicSalary;
    }

    private double getMonthlyLoanInstallmentAmount(Double loanAmount, Double instalments, Double interestRate) {

        if (loanAmount <= 0 || instalments <= 0) {
            return 0.0;
        }

        double totalInterest = loanAmount * interestRate * 0.01;
        double loanAmountWithInterest = loanAmount + totalInterest;

        return loanAmountWithInterest / instalments;

    }

    public ApiResponseDTO<PaySlipResponseDTO> getPaySlipById(Long id) {
        logger.info("Fetching PaySlip with ID: {}", id);

        PaySlipModel paySlip = paySlipRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("PaySlip not found with ID: {}", id);
                    return new PaySlipNotFoundException(PAYSLIP_NOT_FOUND_WITH_ID + id);
                });
        logger.info("Fetched PaySlip with ID: {}", id);

        return new ApiResponseDTO<>("PaySlip Fetched Successfully", convertToResponseDTO(paySlip));
    }

    public ApiResponseDTO<List<PaySlipResponseDTO>> getAllPaySlips() {
        logger.info("Fetching all PaySlips");

        List<PaySlipModel> paySlips = paySlipRepository.findAll();
        logger.info("Fetched {} PaySlips", paySlips.size());

        List<PaySlipResponseDTO> responseDTOs = paySlips.stream()
                .map(this::convertToResponseDTO)
                .toList();

        return new ApiResponseDTO<>("Successfully Fetched All PaySlips", responseDTOs);
    }

    public ApiResponseDTO<Void> deletePaySlip(Long id) {
        logger.info("Deleting PaySlip with ID: {}", id);

        PaySlipModel existingPaySlip = paySlipRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("PaySlip not found with given ID: {}", id);
                    return new PaySlipNotFoundException(PAYSLIP_NOT_FOUND_WITH_ID + id);
                });
        paySlipRepository.delete(existingPaySlip);
        logger.info("Deleted PaySlip with ID: {}", id);

        return new ApiResponseDTO<>("PaySlip Deleted Successfully", null);
    }

    public ApiResponseDTO<Page<PaySlipResponseDTO>> filterPaySlips(
            PaySlipFilterCriteria criteria,
            int page,
            int size) {
        Specification<PaySlipModel> specification = PaySlipSpecification.paySlipSpecification(
                criteria.employeeId(),
                criteria.status(),
                criteria.startDate(),
                criteria.endDate(),
                criteria.payPeriodId()
        );

        Page<PaySlipModel> paySlips = paySlipRepository.findAll(specification, PageRequest.of(page, size));

        return new ApiResponseDTO<>("PaySlips filtered successfully", paySlips.map(this::convertToResponseDTO));
    }

    public ApiResponseDTO<List<AnnualGrossRemunerationDTO>> getEmployeeCountByAnnualSalaryRange(Long year) {
        // Get all months of the given year
        List<Long> payPeriodIds = salaryPayPeriodService.getAllPayPeriodByYear(year)
                .stream()
                .map(SalaryPayPeriodResponseDTO::id)
                .toList();

        List<Object[]> rawResults = paySlipRepository.getEmployeeCountByAnnualSalaryRange(payPeriodIds);

        // Group by salary range and sum counts
        Map<String, Long> groupedResults = rawResults
                .stream()
                .collect(Collectors.groupingBy(
                        row -> (String) row[0],  // Extract salary range
                        Collectors.summingLong(row -> (Long) row[1]) // Sum counts for same range
                ));

        // Convert to DTO list
        List<AnnualGrossRemunerationDTO> response = groupedResults
                .entrySet()
                .stream()
                .map(entry -> new AnnualGrossRemunerationDTO(entry.getValue(), entry.getKey()))
                .collect(Collectors.toList());

        return new ApiResponseDTO<>("Successfully Fetched Employee Count By Salary Ranges", response);
    }

    public ApiResponseDTO<TaxAndRemunerationReportDTO> getEmployeeTaxDeductionAndRemunerationReport(Long year) {

        List<TaxAndRemunerationSummaryDTO> monthlyRemunerationSummary = paySlipRepository.getMonthlyTaxAndRemunerationSummary(year);

        TaxAndRemunerationSummaryDTO totalSummary = monthlyRemunerationSummary.stream()
                .reduce(new TaxAndRemunerationSummaryDTO("", 0.0, 0.0, 0.0, 0.0),
                        (acc, current) -> new TaxAndRemunerationSummaryDTO(
                                "",
                                acc.taxExcludedRemuneration() + current.taxExcludedRemuneration(),
                                acc.taxLiableRemuneration() + current.taxLiableRemuneration(),
                                acc.taxDeduction() + current.taxDeduction(),
                                acc.paymentsMade() + current.paymentsMade()
                        ));

        TaxAndRemunerationReportDTO report = new TaxAndRemunerationReportDTO(
                year,
                monthlyRemunerationSummary,
                totalSummary.taxExcludedRemuneration(),
                totalSummary.taxLiableRemuneration(),
                totalSummary.taxDeduction(),
                totalSummary.paymentsMade()
        );

        return new ApiResponseDTO<>("Successfully fetched Employee Tax Deduction & Remuneration Report", report);
    }

    private void saveTax(double totalTax, Long payslipId) {
        if (totalTax > 0) {

            PaySlipDetailsRequestDTO taxDetailRequest = new PaySlipDetailsRequestDTO(
                    payslipId,
                    null,
                    null,
                    null,
                    null,
                    PaySlipDetailsTypeEnum.DEDUCTION,
                    "Tax Deduction",
                    totalTax
            );
            paySlipDetailsService.createPaySlipDetails(taxDetailRequest);
        }
    }

    private void saveAllowances(List<SalaryAllowanceModel> salaryAllowances, Long payslipId) {
        for (SalaryAllowanceModel allowance : salaryAllowances) {

            PaySlipDetailsRequestDTO detailsRequest = new PaySlipDetailsRequestDTO(
                    payslipId,
                    null,
                    null,
                    allowance.getId(),
                    null,
                    PaySlipDetailsTypeEnum.ADDITION,
                    allowance.getAllowanceType().getName(),
                    allowance.getAmount()
            );
            paySlipDetailsService.createPaySlipDetails(detailsRequest);
        }
    }

    private void saveCommissions(List<SalaryCommissionResponseDTO> salaryCommissions, Long payslipId) {
        for (SalaryCommissionResponseDTO commission : salaryCommissions) {

            PaySlipDetailsRequestDTO detailsRequest = new PaySlipDetailsRequestDTO(
                    payslipId,
                    null,
                    null,
                    null,
                    commission.id(),
                    PaySlipDetailsTypeEnum.ADDITION,
                    commission.commissionTypeName(),
                    commission.amount()
            );
            paySlipDetailsService.createPaySlipDetails(detailsRequest);
        }
    }

    private void saveAdvances(List<SalaryAdvanceResponseDTO> salaryAdvances, Long payslipId) {
        for (SalaryAdvanceResponseDTO advance : salaryAdvances) {

            PaySlipDetailsRequestDTO detailsRequest = new PaySlipDetailsRequestDTO(
                    payslipId,
                    null,
                    advance.id(),
                    null,
                    null,
                    PaySlipDetailsTypeEnum.DEDUCTION,
                    null,
                    advance.advanceAmount()
            );
            paySlipDetailsService.createPaySlipDetails(detailsRequest);
        }
    }

    private void saveLoans(List<LoanResponseDTO> loans, Long paySlipId) {
        for (LoanResponseDTO loan : loans) {

            PaySlipDetailsRequestDTO detailsRequest = new PaySlipDetailsRequestDTO(
                    paySlipId,
                    loan.id(),
                    null,
                    null,
                    null,
                    PaySlipDetailsTypeEnum.DEDUCTION,
                    null,
                    getMonthlyLoanInstallmentAmount(loan.loanAmount(), loan.monthlyInstallments(), loan.interestRate())
            );
            // Update loan remaining amount once we deduct the monthly installment amount
            loanService.updateLoanRemainingAmount(loan.id(), detailsRequest.amount());

            // Save the loan deduction as payslip detail
            paySlipDetailsService.createPaySlipDetails(detailsRequest);
        }
    }

    private void saveEPF(double epfDeduction, Long payslipId) {
        if (epfDeduction > 0) {
            PaySlipDetailsRequestDTO epfDetailRequest = new PaySlipDetailsRequestDTO(
                    payslipId,
                    null,
                    null,
                    null,
                    null,
                    PaySlipDetailsTypeEnum.DEDUCTION,
                    "EPF Deduction",
                    epfDeduction
            );
            paySlipDetailsService.createPaySlipDetails(epfDetailRequest);
        }
    }

    private double calculateTax(double grossSalary) {
        List<TaxResponseDTO> taxBrackets = taxService.getTaxBySalaryRange(grossSalary);

        double remainingSalary = grossSalary;
        double totalTax = 0;

        for (TaxResponseDTO bracket : taxBrackets) {
            double taxableIncome;
            if (bracket.maxSalary() != 0) {
                taxableIncome = Math.min(remainingSalary, bracket.maxSalary() - bracket.minSalary());
            } else {
                taxableIncome = Math.min(remainingSalary, bracket.minSalary());
            }

            if (taxableIncome > 0) {
                totalTax += taxableIncome * (bracket.taxRate() / 100);
                remainingSalary -= taxableIncome; //remainingSalary = remainingSalary - taxableIncome
            }
            if (remainingSalary <= 0) break;  // No more salary left to tax
        }

        return totalTax;
    }

    private void saveEpfEtfContribution(Long employeeId, double companyEpf, double companyEtf, Long payPeriodId, Long paySlipId) {
        if (companyEpf > 0 || companyEtf > 0) {
            EpfEtfContributionRequestDTO contributionRequest = new EpfEtfContributionRequestDTO(
                    paySlipId,
                    companyEpf,
                    companyEtf,
                    employeeId,
                    payPeriodId
            );
            epfEtfContributionService.createEpfEtfContribution(contributionRequest);
        }
    }

    private PaySlipResponseDTO convertToResponseDTO(PaySlipModel paySlip) {
        List<PaySlipDetailsResponseDTO> details = paySlipDetailsRepository.findAllByPaySlipId(paySlip.getId())
                .stream()
                .map(detail -> new PaySlipDetailsResponseDTO(
                        detail.getId(),
                        detail.getPaySlip().getId(),
                        detail.getLoanId(),
                        detail.getAdvanceId(),
                        detail.getSalaryAllowanceId(),
                        detail.getSalaryCommissionId(),
                        detail.getType(),
                        detail.getDescription(),
                        detail.getAmount()
                ))
                .toList();

        return new PaySlipResponseDTO(
                paySlip.getId(),
                paySlip.getSalaryId(),
                paySlip.getBasicSalary(),
                paySlip.getAllowances(),
                paySlip.getCommission(),
                paySlip.getStatus(),
                paySlip.getGrossSalary(),
                paySlip.getSalaryAdvanceDeduction(),
                paySlip.getLoanDeduction(),
                paySlip.getAttendanceDeduction(),
                paySlip.getTaxDeduction(),
                paySlip.getEpfDeduction(),
                paySlip.getLeaveDeduction(),
                paySlip.getNetSalary(),
                paySlip.getStartDate(),
                paySlip.getEndDate(),
                paySlip.getEmployee().getId(),
                paySlip.getTaxExcludedAllowances(),
                paySlip.getTaxLiableAllowances(),
                paySlip.getTaxExcludedCommissions(),
                paySlip.getTaxLiableCommissions(),
                details,
                salaryPayPeriodService.convertToResponseDTO(paySlip.getSalaryPayPeriod())
        );
    }
}
