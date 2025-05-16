package com.mexxar.payroll.employee;

import com.mexxar.payroll.common.enums.StatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

interface EmployeeRepository extends JpaRepository<EmployeeModel, Long>, JpaSpecificationExecutor<EmployeeModel> {
    boolean existsByEmail(String email);

    Page<EmployeeModel> findByStatus(StatusEnum status, Pageable pageable);
}
