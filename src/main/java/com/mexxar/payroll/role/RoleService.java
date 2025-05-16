package com.mexxar.payroll.role;

import com.mexxar.payroll.common.ApiResponseDTO;
import com.mexxar.payroll.permission.PermissionModel;
import com.mexxar.payroll.permission.PermissionResponseDTO;
import com.mexxar.payroll.permission.PermissionService;
import com.mexxar.payroll.role.exception.RoleException;
import com.mexxar.payroll.role.exception.RoleNotFoundException;
import com.mexxar.payroll.user.UserModel;
import com.mexxar.payroll.user.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RoleService {

    private final RoleRepository roleRepository;
    private final UserService userService;
    private final PermissionService permissionService;

    public RoleService(RoleRepository roleRepository, @Lazy UserService userService, PermissionService permissionService) {
        this.roleRepository = roleRepository;
        this.userService = userService;
        this.permissionService = permissionService;
    }

    private static final Logger logger = LogManager.getLogger(RoleService.class);

    private static final String ROLE_NOT_FOUND_MSG = "Role not found with id: ";
    private static final String ROLE_CANNOT_DELETE_MSG = "Cannot delete role as it is assigned to users";
    private static final String PERMISSION_ALREADY_ASSIGNED_MSG = "Permission is already assigned to this role";
    private static final String ROLE_ALREADY_EXIST_MSG = "Role already exists";

    @Transactional
    public ApiResponseDTO<RoleResponseDTO> createRole(RoleRequestDTO request) {
        if (request == null || request.name() == null || request.name().trim().isEmpty()) {
            throw new RoleException("Role name cannot be null or empty");
        }

        logger.info("Creating new role with name: {}", request.name());

        if (roleRepository.findByName(request.name()).isPresent()) {
            throw new RoleException(ROLE_ALREADY_EXIST_MSG);
        }
        RoleModel role = new RoleModel();
        role.setName(request.name());

        if (role.getPermissions() == null) {
            role.setPermissions(new HashSet<>());
        }

        Instant start = Instant.now();
        roleRepository.save(role);
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        logger.info("Role created successfully with id: {} in {} ms", role.getId(), timeElapsed.toMillis());

        return new ApiResponseDTO<>("Role Created Successfully", convertToResponseDTO(role));
    }

    public ApiResponseDTO<RoleResponseDTO> addPermissionsToRole(Long id, List<Long> permissionIds) {
        if (permissionIds == null || permissionIds.isEmpty()) {
            throw new RoleException("Permission list cannot be null or empty");
        }

        logger.info("Adding permissions to role with id: {}", id);

        RoleModel role = roleRepository.findById(id)
                .orElseThrow(() -> new RoleNotFoundException(ROLE_NOT_FOUND_MSG + id));

        for (Long permissionId : permissionIds) {
            PermissionModel permission = permissionService.findById(permissionId);
            if (role.getPermissions().contains(permission)) {
                logger.error("Permission is already assigned to role with id: {}", id);
                throw new RoleException(PERMISSION_ALREADY_ASSIGNED_MSG);
            }
            role.getPermissions().add(permission);
        }

        Instant start = Instant.now();
        roleRepository.save(role);
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        logger.info("Permissions added to role with id: {} in {} ms", id, timeElapsed.toMillis());

        return new ApiResponseDTO<>("Permissions Added To The Role", convertToResponseDTO(role));
    }

    public ApiResponseDTO<RoleResponseDTO> getRoleById(Long id) {
        logger.info("Fetching role with id: {}", id);

        Instant start = Instant.now();
        RoleModel role = roleRepository.findById(id)
                .orElseThrow(() -> new RoleNotFoundException(ROLE_NOT_FOUND_MSG + id));
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        logger.info("Role fetched successfully with id: {} in {} ms", id, timeElapsed.toMillis());

        return new ApiResponseDTO<>("Role Fetched Successfully", convertToResponseDTO(role));
    }

    public ApiResponseDTO<List<RoleResponseDTO>> getAllRoles() {
        logger.info("Fetching all roles");

        Instant start = Instant.now();
        List<RoleResponseDTO> roles = roleRepository.findAll().stream()
                .map(role -> new RoleResponseDTO(
                        role.getId(),
                        role.getName(),
                        role.getPermissions().stream()
                                .map(permission -> new PermissionResponseDTO(
                                        permission.getId(),
                                        permission.getName(),
                                        permission.getDescription()
                                ))
                                .collect(Collectors.toList())
                )).collect(Collectors.toList());
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        logger.info("Total roles found: {} in {} ms", roles.size(), timeElapsed.toMillis());

        return new ApiResponseDTO<>("Successfully Fetched All Roles",  roles);
    }

    @Transactional
    public ApiResponseDTO<RoleResponseDTO> updateRole(Long id, RoleRequestDTO roleDetails) {
        if (id == null || roleDetails == null || roleDetails.name() == null || roleDetails.name().trim().isEmpty()) {
            throw new RoleException("Role ID and name cannot be null or empty");
        }

        logger.info("Updating role with id: {}", id);

        RoleModel role = roleRepository.findById(id)
                .orElseThrow(() -> new RoleNotFoundException(ROLE_NOT_FOUND_MSG + id));
        role.setName(roleDetails.name());

        Instant start = Instant.now();
        roleRepository.save(role);
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        logger.info("Role updated successfully with id: {} in {} ms", role.getId(), timeElapsed.toMillis());

        return new ApiResponseDTO<>("Role Updated Successfully", convertToResponseDTO(role));
    }

    public void deleteRole(Long id) {
        logger.info("Deleting role with id: {}", id);

        RoleModel role = roleRepository.findById(id)
                .orElseThrow(() -> new RoleNotFoundException(ROLE_NOT_FOUND_MSG + id));
        List<UserModel> usersWithRole = userService.findUsersWithRole(role);

        if (!usersWithRole.isEmpty()) {
            throw new RoleException(ROLE_CANNOT_DELETE_MSG);
        }
        Instant start = Instant.now();
        roleRepository.deleteById(id);
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        logger.info("Role deleted successfully with id: {} in {} ms", id, timeElapsed.toMillis());
    }

    public ApiResponseDTO<RoleResponseDTO> removePermissionsFromRole(Long id, Set<Long> permissionIds) {
        if (permissionIds == null || permissionIds.isEmpty()) {
            throw new RoleException("Permission list cannot be null or empty");
        }

        logger.info("Removing permissions from role with id: {}", id);

        RoleModel role = roleRepository.findById(id)
                .orElseThrow(() -> new RoleNotFoundException(ROLE_NOT_FOUND_MSG + id));

        Set<PermissionModel> permissionsToRemove = permissionIds.stream()
                .map(permissionService::findById)
                .collect(Collectors.toSet());

        role.getPermissions().removeAll(permissionsToRemove);

        Instant start = Instant.now();
        roleRepository.save(role);
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        logger.info("Permissions removed from role with id: {} in {} ms", id, timeElapsed.toMillis());

        return new ApiResponseDTO<>("Permissions Removed From The Role", convertToResponseDTO(role));
    }

    public RoleModel findDefaultRole() {
        logger.info("Fetching default role 'Staff'");

        return roleRepository.findByName("Staff")
                .orElseGet(() -> {
                    RoleModel staffRole = new RoleModel();
                    staffRole.setName("Staff");
                    return roleRepository.save(staffRole);
                });
    }

    public List<RoleModel> findRolesWithPermission(PermissionModel permission) {
        logger.info("Finding roles with permission: {}", permission.getName());

        return roleRepository.findByPermissions(permission);
    }

    public RoleModel findById(Long id) {
        logger.info("Finding role with id: {}", id);

        return roleRepository.findById(id)
                .orElseThrow(() -> new RoleNotFoundException(ROLE_NOT_FOUND_MSG + id));
    }

    public ApiResponseDTO<RoleResponseDTO> savePermissionsToRole(Long id, List<Long> permissionIds) {
        if (permissionIds == null || permissionIds.isEmpty()) {
            throw new RoleException("Permission list cannot be null or empty");
        }

        logger.info("Saving permissions to role with id: {}", id);

        RoleModel role = roleRepository.findById(id)
            .orElseThrow(() -> new RoleNotFoundException(ROLE_NOT_FOUND_MSG + id));

        // remove all permissions from the role
        role.getPermissions().removeAll(role.getPermissions());

        // add permissions to the role
        for (Long permissionId : permissionIds) {
            PermissionModel permission = permissionService.findById(permissionId);
            role.getPermissions().add(permission);
        }

        Instant start = Instant.now();
        roleRepository.save(role);
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        logger.info("Permissions saved to role with id: {} in {} ms", id, timeElapsed.toMillis());

        return new ApiResponseDTO<>("Permissions Saved To The Role", convertToResponseDTO(role));
    }

    private RoleResponseDTO convertToResponseDTO(RoleModel role) {
        return new RoleResponseDTO(
                role.getId(),
                role.getName(),
                role.getPermissions()
                        .stream()
                        .map(permission -> new PermissionResponseDTO(
                                permission.getId(),
                                permission.getName(),
                                permission.getDescription()
                        ))
                        .collect(Collectors.toList())
        );
    }
}
