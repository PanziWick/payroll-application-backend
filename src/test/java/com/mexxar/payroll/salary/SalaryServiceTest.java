package com.mexxar.payroll.salary;

import com.mexxar.payroll.common.ApiResponseDTO;
import com.mexxar.payroll.employee.EmployeeModel;
import com.mexxar.payroll.employee.EmployeeService;
import com.mexxar.payroll.salary.exception.SalaryNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SalaryServiceTest {

    @Mock
    private SalaryRepository salaryRepository;

    @Mock
    private EmployeeService employeeService;

    @InjectMocks
    private SalaryService salaryService;

    private SalaryModel salaryModel;
    private SalaryRequestDTO salaryRequestDTO;
    private EmployeeModel employeeModel;

    @BeforeEach
    void setUp() {
        employeeModel = new EmployeeModel();
        employeeModel.setId(1L);

        salaryModel = new SalaryModel();
        salaryModel.setId(1L);
        salaryModel.setBasicSalary(5000.0);
        salaryModel.setStartDate(LocalDate.now());
        salaryModel.setEndDate(LocalDate.now().plusMonths(1));
        salaryModel.setEmployee(employeeModel);

        salaryRequestDTO = new SalaryRequestDTO(
                5000.0,
                LocalDate.now(),
                LocalDate.now().plusMonths(1),
                1L
        );
    }

    @Test
    void should_successfully_createSalary() {
        when(employeeService.getEmployeeModelById(anyLong())).thenReturn(employeeModel);
        when(salaryRepository.save(any(SalaryModel.class))).thenReturn(salaryModel);

        ApiResponseDTO<SalaryResponseDTO> response = salaryService.createSalary(salaryRequestDTO);

        assertNotNull(response);
        assertEquals("Salary Created Successfully", response.getMessage());
        assertEquals(1L, response.getData().id());
        verify(salaryRepository, times(1)).save(any(SalaryModel.class));
    }

    @Test
    void should_throwException_when_getSalaryById_NotFound() {
        when(salaryRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(SalaryNotFoundException.class, () -> salaryService.getSalaryById(1L));
    }

    @Test
    void should_returnSalaryModel_when_salaryExists() {
        Long salaryId = 1L;

        salaryModel.setId(salaryId);
        salaryModel.setBasicSalary(5000.0);

        when(salaryRepository.findById(salaryId)).thenReturn(Optional.of(salaryModel));

        Optional<SalaryModel> result = salaryService.getSalaryModelById(salaryId);

        assertTrue(result.isPresent());
        assertEquals(salaryId, result.get().getId());
        assertEquals(5000.0, result.get().getBasicSalary());

        verify(salaryRepository, times(1)).findById(salaryId);
    }

    @Test
    void should_returnEmptyOptional_when_salaryDoesNotExist() {
        Long salaryId = 1L;
        when(salaryRepository.findById(salaryId)).thenReturn(Optional.empty());

        Optional<SalaryModel> result = salaryService.getSalaryModelById(salaryId);

        assertTrue(result.isEmpty());
        verify(salaryRepository, times(1)).findById(salaryId);
    }

    @Test
    void should_successfully_getSalaryByEmployeeId() {
        when(employeeService.getEmployeeModelById(anyLong())).thenReturn(employeeModel);
        when(salaryRepository.findByEmployeeId(anyLong())).thenReturn(Optional.of(salaryModel));

        SalaryResponseDTO response = salaryService.getSalaryByEmployeeId(1L);

        assertNotNull(response);
        assertEquals(1L, response.id());
        verify(salaryRepository, times(1)).findByEmployeeId(anyLong());
    }

    @Test
    void should_throwException_when_getSalaryByEmployeeId_NotFound() {
        when(employeeService.getEmployeeModelById(anyLong())).thenReturn(employeeModel);
        when(salaryRepository.findByEmployeeId(anyLong())).thenReturn(Optional.empty());

        assertThrows(SalaryNotFoundException.class, () -> salaryService.getSalaryByEmployeeId(1L));
    }

    @Test
    void should_successfully_getAllSalaries() {
        Page<SalaryModel> salaryPage = new PageImpl<>(List.of(salaryModel));
        when(salaryRepository.findAll(any(Pageable.class))).thenReturn(salaryPage);

        ApiResponseDTO<Page<SalaryResponseDTO>> response = salaryService.getAllSalaries(Pageable.unpaged());

        assertNotNull(response);
        assertEquals("Successfully Fetched All Salaries", response.getMessage());
        assertEquals(1, response.getData().getTotalElements());
    }

    @Test
    void should_returnSalary_when_employeeHasSalary() {
        // Given
        Long employeeId = 100L;
        EmployeeModel employee = new EmployeeModel();
        employee.setId(employeeId);

        SalaryModel salary = new SalaryModel();
        salary.setId(1L);
        salary.setBasicSalary(6000.0);
        salary.setEmployee(employee);

        when(employeeService.getEmployeeModelById(employeeId)).thenReturn(employee);
        when(salaryRepository.findByEmployeeId(employeeId)).thenReturn(Optional.of(salary));

        // When
        SalaryResponseDTO result = salaryService.getSalaryByEmployeeId(employeeId);

        // Then
        assertEquals(1L, result.id());
        assertEquals(6000.0, result.basicSalary());
        verify(salaryRepository, times(1)).findByEmployeeId(employeeId);
    }

    @Test
    void should_throwSalaryNotFoundException_when_employeeHasNoSalary() {
        // Given
        Long employeeId = 100L;
        EmployeeModel employee = new EmployeeModel();
        employee.setId(employeeId);

        when(employeeService.getEmployeeModelById(employeeId)).thenReturn(employee);
        when(salaryRepository.findByEmployeeId(employeeId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(SalaryNotFoundException.class, () -> salaryService.getSalaryByEmployeeId(employeeId));
    }


    @Test
    void should_successfully_updateSalary() {
        when(salaryRepository.findById(anyLong())).thenReturn(Optional.of(salaryModel));
        when(salaryRepository.save(any(SalaryModel.class))).thenReturn(salaryModel);

        ApiResponseDTO<SalaryResponseDTO> response = salaryService.updateSalary(1L, salaryRequestDTO);

        assertNotNull(response);
        assertEquals("Salary Updated Successfully", response.getMessage());
        verify(salaryRepository, times(1)).save(any(SalaryModel.class));
    }

    @Test
    void should_throwException_when_updateSalary_NotFound() {
        when(salaryRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(SalaryNotFoundException.class, () -> salaryService.updateSalary(1L, salaryRequestDTO));
    }

    @Test
    void should_successfully_deleteSalary() {
        when(salaryRepository.findById(anyLong())).thenReturn(Optional.of(salaryModel));
        doNothing().when(salaryRepository).delete(any(SalaryModel.class));

        ApiResponseDTO<Void> response = salaryService.deleteSalary(1L);

        assertNotNull(response);
        assertEquals("Salary Deleted Successfully", response.getMessage());
        verify(salaryRepository, times(1)).delete(any(SalaryModel.class));
    }

    @Test
    void should_throwException_when_deleteSalary_NotFound() {
        when(salaryRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(SalaryNotFoundException.class, () -> salaryService.deleteSalary(1L));
    }

    @Test
    void should_throwException_when_createSalary_withNullRequest() {
        assertThrows(NullPointerException.class, () -> salaryService.createSalary(null));
    }

    @Test
    void should_throwException_when_getSalaryById_withNullId() {
        assertThrows(NullPointerException.class, () -> salaryService.getSalaryById(null));
    }

    @Test
    void should_throwException_when_deleteSalary_withNullId() {
        assertThrows(NullPointerException.class, () -> salaryService.deleteSalary(null));
    }
}
