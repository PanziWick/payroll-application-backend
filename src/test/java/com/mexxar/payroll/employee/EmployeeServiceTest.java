package com.mexxar.payroll.employee;

import com.mexxar.payroll.common.ApiResponseDTO;
import com.mexxar.payroll.common.enums.StatusEnum;
import com.mexxar.payroll.department.DepartmentModel;
import com.mexxar.payroll.department.DepartmentService;
import com.mexxar.payroll.designation.DesignationModel;
import com.mexxar.payroll.designation.DesignationService;
import com.mexxar.payroll.employee.enums.GenderEnum;
import com.mexxar.payroll.employee.enums.MaritalEnum;
import com.mexxar.payroll.employee.exception.EmployeeException;
import com.mexxar.payroll.employee.exception.EmployeeNotFoundException;
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
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @InjectMocks
    private EmployeeService employeeService;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private DepartmentService departmentService;

    @Mock
    private DesignationService designationService;

    DepartmentModel department;
    DesignationModel designation;
    EmployeeModel employee1, employee2;
    EmployeeRequestDTO employeeRequestDTO;
    EmployeeFilterCriteria criteria;

    @BeforeEach
    void setUp() {
        department = new DepartmentModel();
        department.setId(1L);
        department.setName("IT");
        department.setStatus(StatusEnum.ACTIVE);

        designation = new DesignationModel();
        designation.setId(1L);
        designation.setJobTitle("Software Developer");
        designation.setJobDescription("Java Software Developer");
        designation.setStatus(StatusEnum.ACTIVE);

        employee1 = new EmployeeModel();
        employee1.setId(1L);
        employee1.setFirstName("Panzi");
        employee1.setMiddleName("Man");
        employee1.setLastName("Wick");
        employee1.setEmail("panzi@gmail.com");
        employee1.setDob(LocalDate.parse("1999-05-05"));
        employee1.setContactNumber("+947788990000");
        employee1.setHireDate(LocalDate.parse("2023-05-05"));
        employee1.setEpfNumber("7777777");
        employee1.setNationalIdNumber("0123456789v");
        employee1.setGender(GenderEnum.MALE);
        employee1.setMarital(MaritalEnum.SINGLE);
        employee1.setDepartment(department);
        employee1.setDesignation(designation);
        employee1.setStatus(StatusEnum.ACTIVE);

        employee2 = new EmployeeModel();
        employee2.setId(2L);
        employee2.setFirstName("Panzix");
        employee2.setMiddleName("Manx");
        employee2.setLastName("Wickx");
        employee2.setEmail("panzix@gmail.com");
        employee2.setDob(LocalDate.parse("2000-01-01"));
        employee2.setContactNumber("+947654321000");
        employee2.setHireDate(LocalDate.parse("2024-05-05"));
        employee2.setEpfNumber("7070707070707");
        employee2.setNationalIdNumber("9876543210v");
        employee2.setGender(GenderEnum.MALE);
        employee2.setMarital(MaritalEnum.SINGLE);
        employee2.setDepartment(department);
        employee2.setDesignation(designation);
        employee2.setStatus(StatusEnum.ACTIVE);

        employeeRequestDTO = new EmployeeRequestDTO(
                "Panzi",
                "Man",
                "Wick",
                "panzi@gmail.com",
                LocalDate.parse("1999-05-05"),
                "+947654321089",
                LocalDate.parse("2023-05-05"),
                "7777777",
                "0123456789v",
                GenderEnum.MALE,
                MaritalEnum.SINGLE,
                1L,
                1L,
                StatusEnum.ACTIVE
        );

        criteria = new EmployeeFilterCriteria(
                "+947788990000",
                LocalDate.parse("2023-01-01"),
                LocalDate.parse("2023-12-31"),
                StatusEnum.ACTIVE,
                1L,
                1L,
                "Panzi"
        );
    }

    @Test
    void should_successfully_create_employee() {
        // Mocking department and designation service calls
        when(departmentService.findDepartmentById(1L)).thenReturn(department);
        when(designationService.findDesignationById(1L)).thenReturn(designation);

        // Mocking employee repository to return the employee after saving
        when(employeeRepository.save(any(EmployeeModel.class))).thenAnswer(invocation -> {
            EmployeeModel savedEmployee = invocation.getArgument(0);
            savedEmployee.setId(1L); // Set the ID to mimic persistence behavior
            return savedEmployee;
        });

        // Call the service method
        ApiResponseDTO<EmployeeResponseDTO> response = employeeService.createEmployee(employeeRequestDTO);

        // Assertions
        assertNotNull(response);
        assertEquals("Employee Created Successfully", response.getMessage());
        EmployeeResponseDTO responseDTO = response.getData();
        assertNotNull(responseDTO);
        assertEquals(employeeRequestDTO.firstName(), responseDTO.firstName());
        assertEquals(employeeRequestDTO.middleName(), responseDTO.middleName());
        assertEquals(employeeRequestDTO.lastName(), responseDTO.lastName());
        assertEquals(employeeRequestDTO.email(), responseDTO.email());
        assertEquals(employeeRequestDTO.dob(), responseDTO.dob());
        assertEquals(employeeRequestDTO.contactNumber(), responseDTO.contactNumber());
        assertEquals(employeeRequestDTO.hireDate(), responseDTO.hireDate());
        assertEquals(employeeRequestDTO.epfNumber(), responseDTO.epfNumber());
        assertEquals(employeeRequestDTO.nationalIdNumber(), responseDTO.nationalIdNumber());
        assertEquals(employeeRequestDTO.gender(), responseDTO.gender());
        assertEquals(employeeRequestDTO.marital(), responseDTO.marital());
        assertEquals(employeeRequestDTO.departmentId(), department.getId());
        assertEquals(employeeRequestDTO.designationId(), designation.getId());
        assertEquals(employeeRequestDTO.status(), responseDTO.status());

        // Verify the save method was called once
        verify(employeeRepository, times(1)).save(any(EmployeeModel.class));
    }

    @Test
    void should_not_create_duplicate_employee() {
        when(employeeRepository.existsByEmail("panzi@gmail.com")).thenReturn(true);

        assertThrows(EmployeeException.class, () -> employeeService.createEmployee(employeeRequestDTO));
        verify(employeeRepository, never()).save(any(EmployeeModel.class));
    }

    @Test
    void should_fail_to_create_employee_with_invalid_department() {
        when(departmentService.findDepartmentById(1L))
                .thenThrow(new EmployeeException("Invalid Department ID"));

        assertThrows(EmployeeException.class, () -> employeeService.createEmployee(employeeRequestDTO));

        verify(departmentService, times(1)).findDepartmentById(1L);
        verify(employeeRepository, never()).save(any(EmployeeModel.class));
    }

    @Test
    void should_fail_to_create_employee_with_invalid_designation() {
        when(designationService.findDesignationById(1L))
                .thenThrow(new EmployeeException("Invalid Designation ID"));

        assertThrows(EmployeeException.class, () -> employeeService.createEmployee(employeeRequestDTO));

        verify(designationService, times(1)).findDesignationById(1L);
        verify(employeeRepository, never()).save(any(EmployeeModel.class));
    }

    @Test
    void should_successfully_get_employee_by_id() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee1));

        ApiResponseDTO<EmployeeResponseDTO> response = employeeService.getEmployeeById(1L);

        assertNotNull(response);
        assertEquals("Employee Fetched Successfully", response.getMessage());
        EmployeeResponseDTO responseDTO = response.getData();
        assertNotNull(responseDTO);
        assertEquals(1L, responseDTO.id());
        assertEquals("Panzi", responseDTO.firstName());
        assertEquals("Man", responseDTO.middleName());
        assertEquals("Wick", responseDTO.lastName());
    }

    @Test
    void should_throw_exception_when_employee_not_found_by_id() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EmployeeNotFoundException.class, () -> employeeService.getEmployeeById(1L));
        verify(employeeRepository, times(1)).findById(1L);
    }

    @Test
    void should_successfully_get_employee_model_by_id() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee1));

        EmployeeModel employeeModel = employeeService.getEmployeeModelById(1L);

        assertNotNull(employeeModel);
        assertEquals(1L, employee1.getId());
        assertEquals("Panzi", employeeModel.getFirstName());
        assertEquals("Man", employeeModel.getMiddleName());
        assertEquals("Wick", employeeModel.getLastName());
    }

    @Test
    void should_successfully_get_all_employees() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<EmployeeModel> employeePage = new PageImpl<>(Arrays.asList(employee1, employee2), pageable, employeeRepository.count());

        when(employeeRepository.findAll(pageable)).thenReturn(employeePage);

        ApiResponseDTO<Page<EmployeeResponseDTO>> employeeResponseDTOPage = employeeService.getAllEmployees(0, 10);

        assertNotNull(employeeResponseDTOPage);
        assertEquals(2, employeeResponseDTOPage.getData().getTotalElements());
        verify(employeeRepository, times(1)).findAll(pageable);
    }

    @Test
    void should_return_empty_list_when_no_employees_exist() {
        Pageable pageable = PageRequest.of(0, 10);
        when(employeeRepository.findAll(pageable)).thenReturn(Page.empty());

        ApiResponseDTO<Page<EmployeeResponseDTO>> response = employeeService.getAllEmployees(0, 10);

        assertTrue(response.getData().isEmpty());
        verify(employeeRepository, times(1)).findAll(pageable);
    }

    @Test
    void should_successfully_get_all_active_employees() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<EmployeeModel> employeePage = new PageImpl<>(Arrays.asList(employee1, employee2), pageable, employeeRepository.count());

        when(employeeRepository.findByStatus(StatusEnum.ACTIVE, pageable)).thenReturn(employeePage);

        ApiResponseDTO<Page<EmployeeResponseDTO>> activeEmployeeResponseDTOPage = employeeService.getAllActiveEmployees(0, 10);

        assertNotNull(activeEmployeeResponseDTOPage);
        assertEquals(2, activeEmployeeResponseDTOPage.getData().getTotalElements());
        verify(employeeRepository, times(1)).findByStatus(StatusEnum.ACTIVE, pageable);
    }

    @Test
    void should_successfully_update_employee() {
        when(employeeRepository.findById(2L)).thenReturn(Optional.of(employee2));
        when(departmentService.findDepartmentById(1L)).thenReturn(department);
        when(designationService.findDesignationById(1L)).thenReturn(designation);
        when(employeeRepository.save(any(EmployeeModel.class))).thenReturn(employee2);

        employeeRequestDTO = new EmployeeRequestDTO(
                "Panzii",
                "Mann",
                "Wickk",
                "panzii@gmail.com",
                LocalDate.parse("1999-10-10"),
                "+94777888999",
                LocalDate.parse("2023-05-05"),
                "77777770",
                "9876543210v",
                GenderEnum.MALE,
                MaritalEnum.SINGLE,
                1L,
                1L,
                StatusEnum.ACTIVE
        );

        ApiResponseDTO<EmployeeResponseDTO> response = employeeService.updateEmployee(2L, employeeRequestDTO);

        assertNotNull(response);
        assertEquals("Employee Updated Successfully", response.getMessage());
        EmployeeResponseDTO responseDTO = response.getData();
        assertNotNull(responseDTO);
        assertEquals(2L, responseDTO.id());
        assertEquals("Panzii", responseDTO.firstName());
        assertEquals("Mann", responseDTO.middleName());
        assertEquals("Wickk", responseDTO.lastName());
        assertEquals("panzii@gmail.com", responseDTO.email());
        assertEquals(LocalDate.parse("1999-10-10"), responseDTO.dob());
        assertEquals("+94777888999", responseDTO.contactNumber());
        assertEquals(LocalDate.parse("2023-05-05"), responseDTO.hireDate());
        assertEquals("77777770", responseDTO.epfNumber());
        assertEquals("9876543210v", responseDTO.nationalIdNumber());
        assertEquals(GenderEnum.MALE, responseDTO.gender());
        assertEquals(MaritalEnum.SINGLE, responseDTO.marital());
        assertEquals(department.getId(), responseDTO.department().getId());
        assertEquals(designation.getId(), responseDTO.designation().getId());
        assertEquals(StatusEnum.ACTIVE, responseDTO.status());

        verify(employeeRepository, times(1)).save(any(EmployeeModel.class));
    }

    @Test
    void should_fail_to_update_nonexistent_employee() {
        when(employeeRepository.findById(3L)).thenReturn(Optional.empty());

        assertThrows(EmployeeNotFoundException.class, () -> employeeService.updateEmployee(3L, employeeRequestDTO));

        verify(employeeRepository, times(1)).findById(3L);
        verify(employeeRepository, never()).save(any(EmployeeModel.class));
    }

    @Test
    void should_successfully_update_employee_status() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee1));
        when(employeeRepository.save(any(EmployeeModel.class))).thenReturn(employee1);

        ApiResponseDTO<EmployeeResponseDTO> responseDTO = employeeService.updateEmployeeStatus(1L, "INACTIVE");

        assertNotNull(responseDTO);
        assertEquals(StatusEnum.INACTIVE, responseDTO.getData().status());
        verify(employeeRepository, times(1)).save(employee1);
    }

    @Test
    void should_successfully_delete_employee() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee1));
        when(employeeRepository.save(any(EmployeeModel.class))).thenReturn(employee1);

        employeeService.deleteEmployee(1L);

        assertEquals(StatusEnum.INACTIVE, employee1.getStatus());
        verify(employeeRepository, times(1)).save(employee1);
    }

    @Test
    void should_successfully_filter_employees() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<EmployeeModel> employeePage = new PageImpl<>(Arrays.asList(employee1, employee2), pageable, 2);

        when(employeeRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(employeePage);

        ApiResponseDTO<Page<EmployeeResponseDTO>> response = employeeService.filterEmployees(criteria, 0, 10);

        assertNotNull(response);
        assertEquals("Filtering Employee Successfully", response.getMessage());
        assertNotNull(response.getData());
        assertEquals(2, response.getData().getTotalElements());
        assertEquals(2, response.getData().getContent().size());
        assertEquals("Panzi", response.getData().getContent().get(0).firstName());
        assertEquals("Panzix", response.getData().getContent().get(1).firstName());

        verify(employeeRepository, times(1)).findAll(any(Specification.class), eq(pageable));
    }
}
