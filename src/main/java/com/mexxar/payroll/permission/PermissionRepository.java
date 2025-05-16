package com.mexxar.payroll.permission;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

interface PermissionRepository extends JpaRepository<PermissionModel, Long> {
    Optional<PermissionModel> findByName(String name);
}
