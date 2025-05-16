package com.mexxar.payroll.department;

import com.mexxar.payroll.common.enums.StatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

interface DepartmentRepository extends JpaRepository<DepartmentModel, Long> {
    Optional<DepartmentModel> findByName(String name);

    Page<DepartmentModel> findAll(Pageable pageable);

    Page<DepartmentModel> findByStatus(StatusEnum status, Pageable pageable);
}
