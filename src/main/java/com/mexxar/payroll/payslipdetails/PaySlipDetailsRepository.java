package com.mexxar.payroll.payslipdetails;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaySlipDetailsRepository extends JpaRepository<PaySlipDetailsModel, Long> {
    List<PaySlipDetailsModel> findAllByPaySlipId(Long paySlipId);
}
