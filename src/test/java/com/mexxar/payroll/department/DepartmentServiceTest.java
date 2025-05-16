package com.mexxar.payroll.department;

import com.mexxar.payroll.common.ApiResponseDTO;
import com.mexxar.payroll.common.enums.StatusEnum;
import com.mexxar.payroll.department.exception.DepartmentException;
import com.mexxar.payroll.department.exception.DepartmentNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Optional;

import static com.mexxar.payroll.common.enums.StatusEnum.ACTIVE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DepartmentServiceTest {

    @InjectMocks
    private DepartmentService departmentService;

    @Mock
    private DepartmentRepository departmentRepository;

    DepartmentModel department1, department2, department3;

    DepartmentRequestDTO requestDTO;

    @BeforeEach
    void setup() {
        department1 = new DepartmentModel();
        department1.setId(1L);
        department1.setName("IT");
        department1.setStatus(ACTIVE);

        department2 = new DepartmentModel();
        department2.setId(2L);
        department2.setName("HR");
        department2.setStatus(StatusEnum.INACTIVE);

        department3 = new DepartmentModel();
        department3.setId(3L);
        department3.setName("Finance");
        department3.setStatus(ACTIVE);

        requestDTO = new DepartmentRequestDTO(
                "IT",
                ACTIVE
        );
    }

    @Test
    void should_successfully_create_department() {
        when(departmentRepository.save(any(DepartmentModel.class))).thenReturn(department1);

        DepartmentModel department = new DepartmentModel();
        department.setName("IT");

        ApiResponseDTO<DepartmentResponseDTO> response = departmentService.createDepartment(requestDTO);

        assertNotNull(response);
        assertEquals("Department Created Successfully", response.getMessage());
        DepartmentResponseDTO responseDTO = response.getData();
        assertNotNull(responseDTO);
        assertEquals(1L, responseDTO.id());
        assertEquals("IT", responseDTO.name());
        assertEquals(ACTIVE, responseDTO.status());
        verify(departmentRepository, times(1)).save(any(DepartmentModel.class));
    }

    @Test
    void should_not_create_duplicate_department() {
        when(departmentRepository.findByName("IT")).thenReturn(Optional.of(department1));

        DepartmentModel department = new DepartmentModel();
        department.setName("IT");

        assertThrows(DepartmentException.class, () -> departmentService.createDepartment(requestDTO));
        verify(departmentRepository, never()).save(any(DepartmentModel.class));
    }

    @Test
    void should_throw_exception_when_creating_department_with_null_request() {
        DepartmentRequestDTO nullRequest = null;

        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            departmentService.createDepartment(nullRequest);
        });

        assertEquals("Department request must not be null", exception.getMessage());
    }

    @Test
    void should_successfully_get_department_by_id() {
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department1));

        ApiResponseDTO<DepartmentResponseDTO> response = departmentService.getDepartmentById(1L);

        assertNotNull(response);
        assertEquals("Department Fetched Successfully", response.getMessage());
        DepartmentResponseDTO responseDTO = response.getData();
        assertEquals(1L, responseDTO.id());
        assertEquals("IT", responseDTO.name());
        assertEquals(ACTIVE, responseDTO.status());
        verify(departmentRepository, times(1)).findById(1L);
    }

    @Test
    void should_throw_exception_if_department_not_found_by_id() {
        when(departmentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(DepartmentNotFoundException.class, () -> departmentService.getDepartmentById(1L));
        verify(departmentRepository, times(1)).findById(1L);
    }

    @Test
    void should_successfully_get_FindById() {
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department1));

        DepartmentModel foundDepartment = departmentService.findDepartmentById(1L);

        assertNotNull(foundDepartment);
        assertEquals(department1.getId(), foundDepartment.getId());
        assertEquals(department1.getName(), foundDepartment.getName());
        assertEquals(department1.getStatus(), foundDepartment.getStatus());
    }

    @Test
    void should_successfully_get_FindById_DepartmentNotFound() {
        when(departmentRepository.findById(1L)).thenReturn(Optional.empty());

        DepartmentException exception = assertThrows(DepartmentException.class,
                () -> departmentService.findDepartmentById(1L)
        );

        assertEquals("Department not found with id: " + 1L, exception.getMessage());
    }

    @Test
    void should_successfully_get_all_departments() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<DepartmentModel> departmentPage = new PageImpl<>(Arrays.asList(department1, department2, department3), pageable, 3);

        when(departmentRepository.findAll(pageable)).thenReturn(departmentPage);

        ApiResponseDTO<Page<DepartmentResponseDTO>> departmentResponseDTOS = departmentService.getAllDepartments(0, 10);

        assertNotNull(departmentResponseDTOS);
        assertEquals(3, departmentResponseDTOS.getData().getTotalElements());
        verify(departmentRepository, times(1)).findAll(pageable);
    }

    @Test
    void should_return_empty_page_when_no_departments_exist() {
        Pageable pageable = PageRequest.of(0, 10);
        when(departmentRepository.findAll(pageable)).thenReturn(Page.empty());

        ApiResponseDTO<Page<DepartmentResponseDTO>> departmentResponseDTOS = departmentService.getAllDepartments(0, 10);

        assertNotNull(departmentResponseDTOS);
        assertEquals(0, departmentResponseDTOS.getData().getTotalElements());
        verify(departmentRepository, times(1)).findAll(pageable);
    }

    @Test
    void should_successfully_get_all_active_departments() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<DepartmentModel> departmentPage = new PageImpl<>(Arrays.asList(department1, department2, department3), pageable, 2);

        when(departmentRepository.findByStatus(ACTIVE, pageable)).thenReturn(departmentPage);

        ApiResponseDTO<Page<DepartmentResponseDTO>> activeDepartments = departmentService.getAllActiveDepartments(0, 10);

        assertNotNull(activeDepartments);
        assertEquals(3, activeDepartments.getData().getTotalElements());
        verify(departmentRepository, times(1)).findByStatus(ACTIVE, pageable);
    }

    @Test
    void should_successfully_update_department() {

        requestDTO = new DepartmentRequestDTO(
                "IT Updated",
                ACTIVE
        );

        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department1));
        when(departmentRepository.save(any(DepartmentModel.class))).thenReturn(department1);

        DepartmentModel department = new DepartmentModel();
        department.setName("IT");

        ApiResponseDTO<DepartmentResponseDTO> response = departmentService.updateDepartment(1L, requestDTO);

        assertNotNull(response);
        assertEquals("Department Updated Successfully", response.getMessage());
        DepartmentResponseDTO responseDTO = response.getData();
        assertNotNull(responseDTO);
        assertEquals(1L, responseDTO.id());
        assertEquals("IT Updated", responseDTO.name());
        verify(departmentRepository, times(1)).save(any(DepartmentModel.class));
    }

    @Test
    void should_throw_exception_when_updating_department_with_null_request() {
        DepartmentRequestDTO nullRequest = null;

        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            departmentService.updateDepartment(1L, nullRequest);
        });

        assertEquals("Department request must not be null", exception.getMessage());
    }

    @Test
    void should_successfully_update_department_status() {
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department1));
        when(departmentRepository.save(department1)).thenReturn(department1);

        ApiResponseDTO<DepartmentResponseDTO> response = departmentService.updateDepartmentStatus(1L, "ACTIVE");

        assertNotNull(response);
        assertEquals("Department Status Updated Successfully", response.getMessage());
        DepartmentResponseDTO responseDTO = response.getData();
        assertNotNull(responseDTO);
        assertEquals(ACTIVE, responseDTO.status());
        verify(departmentRepository, times(1)).findById(1L);
        verify(departmentRepository, times(1)).save(department1);
    }

    @Test
    void should_successfully_delete_department() {
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department1));

        departmentService.deleteDepartment(1L);

        verify(departmentRepository, times(1)).findById(1L);
        assertEquals(StatusEnum.INACTIVE, department1.getStatus());
        verify(departmentRepository, times(1)).save(department1);
    }

    @Test
    void should_throw_exception_if_department_to_delete_not_found() {
        when(departmentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(DepartmentNotFoundException.class, () -> departmentService.deleteDepartment(1L));
        verify(departmentRepository, times(1)).findById(1L);
    }
}
