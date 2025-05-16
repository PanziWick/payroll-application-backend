package com.mexxar.payroll.leave;

import org.springframework.data.jpa.repository.JpaRepository;

interface LeavePolicyRepository extends JpaRepository<LeavePolicyModel, Long> {
}
