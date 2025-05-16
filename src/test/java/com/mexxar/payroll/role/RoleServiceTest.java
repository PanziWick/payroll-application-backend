package com.mexxar.payroll.role;

import com.mexxar.payroll.common.ApiResponseDTO;
import com.mexxar.payroll.permission.PermissionModel;
import com.mexxar.payroll.permission.PermissionService;
import com.mexxar.payroll.role.exception.RoleException;
import com.mexxar.payroll.role.exception.RoleNotFoundException;
import com.mexxar.payroll.user.UserModel;
import com.mexxar.payroll.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @InjectMocks
    private RoleService roleService;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserService userService;

    @Mock
    private PermissionService permissionService;

    private RoleModel role;
    private RoleRequestDTO roleRequestDTO;
    private PermissionModel permission;

    @BeforeEach
    void setUp() {
        role = new RoleModel();
        role.setId(1L);
        role.setName("Admin");

        permission = new PermissionModel();
        permission.setId(1L);
        permission.setName("READ");

        roleRequestDTO = new RoleRequestDTO("Admin", Collections.singletonList(1L));
    }

    @Test
    void should_successfully_create_role() {
        when(roleRepository.findByName("Admin")).thenReturn(Optional.empty());
        when(roleRepository.save(any(RoleModel.class))).thenReturn(role);

        ApiResponseDTO<RoleResponseDTO> response = roleService.createRole(roleRequestDTO);

        assertNotNull(response);
        assertEquals("Role Created Successfully", response.getMessage());
        RoleResponseDTO responseDTO = response.getData();
        assertEquals("Admin", responseDTO.name());
        assertTrue(responseDTO.permissions().isEmpty());

        verify(roleRepository, times(1)).save(any(RoleModel.class));
    }

    @Test
    void should_throw_exception_when_creating_role_with_existing_name() {
        when(roleRepository.findByName("Admin")).thenReturn(Optional.of(role));

        RoleException exception = assertThrows(RoleException.class, () ->
                roleService.createRole(roleRequestDTO)
        );

        assertEquals("Role already exists", exception.getMessage());
    }

    @Test
    void should_throw_exception_when_creating_role_with_null_request() {
        RoleException exception = assertThrows(RoleException.class, () ->
                roleService.createRole(null)
        );

        assertEquals("Role name cannot be null or empty", exception.getMessage());
    }

    @Test
    void should_throw_exception_when_creating_role_with_empty_name() {
        RoleRequestDTO invalidRequest = new RoleRequestDTO("", Collections.emptyList());

        RoleException exception = assertThrows(RoleException.class, () ->
                roleService.createRole(invalidRequest)
        );

        assertEquals("Role name cannot be null or empty", exception.getMessage());
    }

    @Test
    void should_successfully_add_permissions_to_role() {
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(permissionService.findById(1L)).thenReturn(permission);

        ApiResponseDTO<RoleResponseDTO> response = roleService.addPermissionsToRole(1L, List.of(1L));

        assertNotNull(response);
        assertEquals("Permissions Added To The Role", response.getMessage());
        RoleResponseDTO responseDTO = response.getData();
        assertEquals("Admin", responseDTO.name());
        assertEquals(1, responseDTO.permissions().size());
        assertEquals("READ", responseDTO.permissions().getFirst());

        verify(roleRepository, times(1)).save(any(RoleModel.class));
    }

    @Test
    void should_throw_exception_when_adding_duplicate_permission_to_role() {
        role.getPermissions().add(permission);
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(permissionService.findById(1L)).thenReturn(permission);

        RoleException exception = assertThrows(RoleException.class, () ->
                roleService.addPermissionsToRole(1L, List.of(1L))
        );

        assertEquals("Permission is already assigned to this role", exception.getMessage());
    }

    @Test
    void should_throw_exception_when_adding_permissions_with_null_list() {
        RoleException exception = assertThrows(RoleException.class, () ->
                roleService.addPermissionsToRole(1L, null)
        );

        assertEquals("Permission list cannot be null or empty", exception.getMessage());
    }

    @Test
    void should_successfully_get_role_by_id() {
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));

        ApiResponseDTO<RoleResponseDTO> response = roleService.getRoleById(1L);

        assertNotNull(response);
        assertEquals("Role Fetched Successfully", response.getMessage());
        RoleResponseDTO responseDTO = response.getData();
        assertEquals("Admin", responseDTO.name());
        assertTrue(responseDTO.permissions().isEmpty());
    }

    @Test
    void should_throw_exception_when_role_not_found_by_id() {
        when(roleRepository.findById(1L)).thenReturn(Optional.empty());

        RoleNotFoundException exception = assertThrows(RoleNotFoundException.class, () ->
                roleService.getRoleById(1L)
        );

        assertEquals("Role not found with id: 1", exception.getMessage());
    }

    @Test
    void should_successfully_get_all_roles() {
        when(roleRepository.findAll()).thenReturn(List.of(role));

        ApiResponseDTO<List<RoleResponseDTO>> response = roleService.getAllRoles();

        assertNotNull(response);
        assertEquals("Successfully Fetched All Roles", response.getMessage());
        assertEquals(1, response.getData().size());
        assertEquals("Admin", response.getData().getFirst().name());
    }

    @Test
    void should_return_empty_list_when_no_roles_found() {
        when(roleRepository.findAll()).thenReturn(Collections.emptyList());

        ApiResponseDTO<List<RoleResponseDTO>> response = roleService.getAllRoles();

        assertNotNull(response);
        assertEquals("Successfully Fetched All Roles", response.getMessage());
        assertTrue(response.getData().isEmpty());
    }

    @Test
    void should_successfully_update_role() {
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(roleRepository.save(any(RoleModel.class))).thenReturn(role);

        ApiResponseDTO<RoleResponseDTO> response = roleService.updateRole(1L, roleRequestDTO);

        assertNotNull(response);
        assertEquals("Role Updated Successfully", response.getMessage());
        RoleResponseDTO responseDTO = response.getData();
        assertEquals("Admin", responseDTO.name());
        assertTrue(responseDTO.permissions().isEmpty());

        verify(roleRepository, times(1)).save(any(RoleModel.class));
    }

    @Test
    void should_throw_exception_when_updating_nonexistent_role() {
        when(roleRepository.findById(1L)).thenReturn(Optional.empty());

        RoleNotFoundException exception = assertThrows(RoleNotFoundException.class, () ->
                roleService.updateRole(1L, roleRequestDTO)
        );

        assertEquals("Role not found with id: 1", exception.getMessage());
    }

    @Test
    void should_throw_exception_when_updating_role_with_null_details() {
        RoleException exception = assertThrows(RoleException.class, () ->
                roleService.updateRole(1L, null)
        );

        assertEquals("Role ID and name cannot be null or empty", exception.getMessage());
    }

    @Test
    void should_throw_exception_when_updating_role_with_empty_name() {
        RoleRequestDTO invalidRequest = new RoleRequestDTO("", Collections.emptyList());

        RoleException exception = assertThrows(RoleException.class, () ->
                roleService.updateRole(1L, invalidRequest)
        );

        assertEquals("Role ID and name cannot be null or empty", exception.getMessage());
    }

    @Test
    void should_successfully_delete_role() {
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(userService.findUsersWithRole(role)).thenReturn(Collections.emptyList());
        doNothing().when(roleRepository).deleteById(1L);

        roleService.deleteRole(1L);

        verify(roleRepository, times(1)).deleteById(1L);
    }

    @Test
    void should_throw_exception_when_deleting_role_assigned_to_users() {
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(userService.findUsersWithRole(role)).thenReturn(List.of(new UserModel()));

        RoleException exception = assertThrows(RoleException.class, () ->
                roleService.deleteRole(1L)
        );

        assertEquals("Cannot delete role as it is assigned to users", exception.getMessage());
    }

    @Test
    void should_successfully_remove_permissions_from_role() {
        role.getPermissions().add(permission);
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(permissionService.findById(1L)).thenReturn(permission);

        ApiResponseDTO<RoleResponseDTO> response = roleService.removePermissionsFromRole(1L, Set.of(1L));

        assertNotNull(response);
        assertEquals("Permissions Removed From The Role", response.getMessage());
        RoleResponseDTO responseDTO = response.getData();
        assertEquals("Admin", responseDTO.name());
        assertTrue(responseDTO.permissions().isEmpty());

        verify(roleRepository, times(1)).save(any(RoleModel.class));
    }

    @Test
    void should_throw_exception_when_removing_permissions_with_null_list() {
        RoleException exception = assertThrows(RoleException.class, () ->
                roleService.removePermissionsFromRole(1L, null)
        );

        assertEquals("Permission list cannot be null or empty", exception.getMessage());
    }

    @Test
    void should_successfully_find_default_role() {
        when(roleRepository.findByName("Staff")).thenReturn(Optional.of(role));

        RoleModel defaultRole = roleService.findDefaultRole();

        assertNotNull(defaultRole);
        assertEquals("Admin", defaultRole.getName());
    }

    @Test
    void should_create_default_role_if_not_found() {
        when(roleRepository.findByName("Staff")).thenReturn(Optional.empty());
        when(roleRepository.save(any(RoleModel.class))).thenReturn(role);

        RoleModel defaultRole = roleService.findDefaultRole();

        assertNotNull(defaultRole);
        assertEquals("Admin", defaultRole.getName());

        verify(roleRepository, times(1)).save(any(RoleModel.class));
    }

    @Test
    void should_successfully_find_roles_with_permission() {
        when(roleRepository.findByPermissions(permission)).thenReturn(List.of(role));

        List<RoleModel> roles = roleService.findRolesWithPermission(permission);

        assertNotNull(roles);
        assertEquals(1, roles.size());
        assertEquals("Admin", roles.getFirst().getName());
    }

    @Test
    void should_successfully_find_role_by_id() {
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));

        RoleModel foundRole = roleService.findById(1L);

        assertNotNull(foundRole);
        assertEquals("Admin", foundRole.getName());
    }

    @Test
    void should_throw_exception_when_finding_nonexistent_role_by_id() {
        when(roleRepository.findById(1L)).thenReturn(Optional.empty());

        RoleNotFoundException exception = assertThrows(RoleNotFoundException.class, () ->
                roleService.findById(1L)
        );

        assertEquals("Role not found with id: 1", exception.getMessage());
    }
}
