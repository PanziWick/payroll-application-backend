package com.mexxar.payroll.employeeleave;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

interface EmployeeLeaveRepository extends JpaRepository<EmployeeLeaveModel, Long> {
    List<EmployeeLeaveModel> findByEmployeeIdAndStatusAndYear(Long employeeId, EmployeeLeaveEnum status, Long year);

    @Query("SELECT COALESCE(SUM(e.numberOfDays), 0) FROM EmployeeLeaveModel e " +
            "WHERE e.employee.id = :employeeId " +
            "AND e.leavePolicy.leaveType = 1 " +
            "AND e.status = 1 " +
            "AND e.startDate >= :startDate " +
            "AND e.endDate <= :endDate")
    Double getTotalNoPayLeaveDays(@Param("employeeId") Long employeeId,
                                  @Param("startDate") LocalDate startDate,
                                  @Param("endDate") LocalDate endDate);
}
