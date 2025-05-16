package com.mexxar.payroll.user;

import com.mexxar.payroll.role.RoleModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

interface UserRepository extends JpaRepository<UserModel, Long> {
    List<UserModel> findByRoles(RoleModel role);

    Optional<UserModel> findByUserName(String userName);

    Optional<UserModel> findByEmail(String email);
}
