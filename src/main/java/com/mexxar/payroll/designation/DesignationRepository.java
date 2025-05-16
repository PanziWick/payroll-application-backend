package com.mexxar.payroll.designation;

import com.mexxar.payroll.common.enums.StatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

interface DesignationRepository extends JpaRepository<DesignationModel, Long> {
    Optional<DesignationModel> findByJobTitleAndJobDescription(String jobTitle, String jobDescription);

    Page<DesignationModel> findAll(Pageable pageable);

    Page<DesignationModel> findByStatus(StatusEnum status, Pageable pageable);
}
