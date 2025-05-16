package com.mexxar.payroll.permission;

import com.mexxar.payroll.common.ApiResponseDTO;
import com.mexxar.payroll.permission.exception.PermissionException;
import com.mexxar.payroll.permission.exception.PermissionNotFoundException;
import com.mexxar.payroll.role.RoleModel;
import com.mexxar.payroll.role.RoleService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PermissionService {

    private final PermissionRepository permissionRepository;
    private final RoleService roleService;

    public PermissionService(PermissionRepository permissionRepository, @Lazy RoleService roleService) {
        this.permissionRepository = permissionRepository;
        this.roleService = roleService;
    }

    private static final Logger logger = LogManager.getLogger(PermissionService.class);

    private static final String PERMISSION_NOT_FOUND_MSG = "Permission not found with id: ";
    private static final String PERMISSION_ALREADY_EXIST_MSG = "Permission already exists";
    private static final String PERMISSION_CANNOT_DELETE_MSG = "Cannot delete permission as it is assigned to roles";

    @Transactional
    public ApiResponseDTO<PermissionResponseDTO> createPermission(PermissionRequestDTO request) {
        logger.debug("Creating new permission with name: {}", request.name());

        if (permissionRepository.findByName(request.name()).isPresent()) {
            throw new PermissionException(PERMISSION_ALREADY_EXIST_MSG);
        }

        PermissionModel permission = new PermissionModel();
        permission.setName(request.name());
        permission.setDescription(request.description());

        Instant start = Instant.now();
        permissionRepository.save(permission);
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        logger.info("Permission created successfully with id: {} in {} ms", permission.getId(), timeElapsed.toMillis());

        return new ApiResponseDTO<>("Permission Created Successfully", convertToResponseDTO(permission));
    }

    public ApiResponseDTO<PermissionResponseDTO> getPermissionById(Long id) {
        logger.info("Fetching permission with id: {}", id);

        PermissionModel permission = permissionRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error(PERMISSION_NOT_FOUND_MSG + "{}", id);
                    return new PermissionNotFoundException(PERMISSION_NOT_FOUND_MSG + id);
                });
        logger.info("Permission fetched successfully with id: {}", id);

        return new ApiResponseDTO<>("Permission Fetched Successfully",  convertToResponseDTO(permission));
    }

    public ApiResponseDTO<List<PermissionResponseDTO>> getAllPermissions() {
        logger.info("Fetching all permissions");

        Instant start = Instant.now();
        List<PermissionResponseDTO> permissions = permissionRepository.findAll().stream().map(permission ->
                new PermissionResponseDTO(
                        permission.getId(),
                        permission.getName(),
                        permission.getDescription()
                )
        ).collect(Collectors.toList());
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        logger.info("Total permissions found: {} in {} ms", permissions.size(), timeElapsed.toMillis());

        return new ApiResponseDTO<>("Successfully Fetched All Permissions",  permissions);
    }

    @Transactional
    public ApiResponseDTO<PermissionResponseDTO> updatePermission(Long id, PermissionRequestDTO request) {
        logger.info("Updating permission with id: {}", id);

        PermissionModel permission = permissionRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error(PERMISSION_NOT_FOUND_MSG + "{}", id);
                    return new PermissionNotFoundException(PERMISSION_NOT_FOUND_MSG + id);
                });

        permission.setName(request.name());
        permission.setDescription(request.description());

        Instant start = Instant.now();
        permissionRepository.save(permission);
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        logger.info("Permission updated successfully with id: {} in {} ms", permission.getId(), timeElapsed.toMillis());

        return new ApiResponseDTO<>("Permission Updated Successfully",  convertToResponseDTO(permission));
    }

    public void deletePermission(Long id) {
        logger.info("Deleting permission with id: {}", id);

        PermissionModel permission = permissionRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error(PERMISSION_NOT_FOUND_MSG + "{}", id);
                    return new PermissionNotFoundException(PERMISSION_NOT_FOUND_MSG + id);
                });
        List<RoleModel> roleWithPermission = roleService.findRolesWithPermission(permission);

        if (!roleWithPermission.isEmpty()) {
            logger.error("Permission deletion failed - {}", PERMISSION_CANNOT_DELETE_MSG);
            throw new PermissionException(PERMISSION_CANNOT_DELETE_MSG);
        }

        Instant start = Instant.now();
        permissionRepository.delete(permission);
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        logger.info("Permission deleted successfully with id: {} in {} ms", id, timeElapsed.toMillis());
    }

    public PermissionModel findById(Long id) {
        logger.info("Finding permission with id: {}", id);

        return permissionRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error(PERMISSION_NOT_FOUND_MSG + "{}", id);
                    return new PermissionNotFoundException(PERMISSION_NOT_FOUND_MSG + id);
                });
    }

    private PermissionResponseDTO convertToResponseDTO(PermissionModel permission) {
        return new PermissionResponseDTO(
                permission.getId(),
                permission.getName(),
                permission.getDescription()
        );
    }
}
