package com.mexxar.payroll.role;

import com.mexxar.payroll.permission.PermissionModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

interface RoleRepository extends JpaRepository<RoleModel, Long> {
    Optional<RoleModel> findByName(String name);

    List<RoleModel> findByPermissions(PermissionModel permissions);
}
