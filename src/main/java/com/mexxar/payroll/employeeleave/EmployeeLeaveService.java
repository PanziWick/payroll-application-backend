package com.mexxar.payroll.employeeleave;

import com.mexxar.payroll.common.ApiResponseDTO;
import com.mexxar.payroll.employee.EmployeeModel;
import com.mexxar.payroll.employee.EmployeeService;
import com.mexxar.payroll.employeeleave.exception.EmployeeLeaveNotFoundException;
import com.mexxar.payroll.leave.LeavePolicyModel;
import com.mexxar.payroll.leave.LeavePolicyService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeLeaveService {

    private final EmployeeLeaveRepository employeeLeaveRepository;
    private final EmployeeService employeeService;
    private final LeavePolicyService leavePolicyService;

    public EmployeeLeaveService(EmployeeLeaveRepository employeeLeaveRepository,
                                EmployeeService employeeService,
                                LeavePolicyService leavePolicyService) {
        this.employeeLeaveRepository = employeeLeaveRepository;
        this.employeeService = employeeService;
        this.leavePolicyService = leavePolicyService;
    }

    private static final String EMPLOYEE_LEAVE_NOT_FOUND_MSG = "Employee Leave not found with id: ";

    public ApiResponseDTO<EmployeeLeaveResponseDTO> createEmployeeLeave(EmployeeLeaveRequestDTO requestDTO) {
        EmployeeModel employee = employeeService.getEmployeeModelById(requestDTO.employeeId());
        LeavePolicyModel leavePolicy = leavePolicyService.getLeavePolicyModelById(requestDTO.leavePolicyId());

        EmployeeLeaveModel employeeLeave = new EmployeeLeaveModel();
        employeeLeave.setStartDate(requestDTO.startDate());
        employeeLeave.setEndDate(requestDTO.endDate());
        employeeLeave.setNumberOfDays(requestDTO.numberOfDays());
        employeeLeave.setStatus(requestDTO.status());
        employeeLeave.setApprovedBy(requestDTO.approvedBy());
        employeeLeave.setEmployee(employee);
        employeeLeave.setLeavePolicy(leavePolicy);

        EmployeeLeaveModel savedLeave = employeeLeaveRepository.save(employeeLeave);

        return new ApiResponseDTO<>("Employee Leave Created Successfully", convertToResponseDTO(savedLeave));
    }

    public ApiResponseDTO<EmployeeLeaveResponseDTO> getEmployeeLeaveById(Long id) {
        EmployeeLeaveModel employeeLeave = employeeLeaveRepository.findById(id)
                .orElseThrow(() -> new EmployeeLeaveNotFoundException(EMPLOYEE_LEAVE_NOT_FOUND_MSG + id));

        return new ApiResponseDTO<>("Successfully Fetched Employee Leave", convertToResponseDTO(employeeLeave));
    }

    public ApiResponseDTO<List<EmployeeLeaveResponseDTO>> getAllEmployeeLeaves() {
        List<EmployeeLeaveResponseDTO> employeeLeaves = employeeLeaveRepository.findAll().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());

        return new ApiResponseDTO<>("Successfully Fetched All Employee Leaves", employeeLeaves);
    }

    public Double getTotalNoPayLeaveDays(Long employeeId, LocalDate startDate, LocalDate endDate) {
        return employeeLeaveRepository.getTotalNoPayLeaveDays(
                employeeId,
                startDate,
                endDate
        );
    }

    public ApiResponseDTO<EmployeeLeaveResponseDTO> updateEmployeeLeave(Long id, EmployeeLeaveRequestDTO requestDTO) {
        EmployeeLeaveModel employeeLeave = employeeLeaveRepository.findById(id)
                .orElseThrow(() -> new EmployeeLeaveNotFoundException(EMPLOYEE_LEAVE_NOT_FOUND_MSG + id));

        EmployeeModel employee = employeeService.getEmployeeModelById(requestDTO.employeeId());
        LeavePolicyModel leavePolicy = leavePolicyService.getLeavePolicyModelById(requestDTO.leavePolicyId());

        employeeLeave.setStartDate(requestDTO.startDate());
        employeeLeave.setEndDate(requestDTO.endDate());
        employeeLeave.setNumberOfDays(requestDTO.numberOfDays());
        employeeLeave.setStatus(requestDTO.status());
        employeeLeave.setApprovedBy(requestDTO.approvedBy());
        employeeLeave.setEmployee(employee);
        employeeLeave.setLeavePolicy(leavePolicy);

        EmployeeLeaveModel updatedLeave = employeeLeaveRepository.save(employeeLeave);

        return new ApiResponseDTO<>("Successfully Updated Employee Leave", convertToResponseDTO(updatedLeave));
    }

    public ApiResponseDTO<EmployeeLeaveResponseDTO> updateEmployeeLeaveStatus(Long id, EmployeeLeaveEnum status) {
        EmployeeLeaveModel employeeLeave = employeeLeaveRepository.findById(id)
                .orElseThrow(() -> new EmployeeLeaveNotFoundException(EMPLOYEE_LEAVE_NOT_FOUND_MSG + id));

        employeeLeave.setStatus(status);
        EmployeeLeaveModel updatedLeave = employeeLeaveRepository.save(employeeLeave);

        return new ApiResponseDTO<>("Successfully Updated Employee Leave Status", convertToResponseDTO(updatedLeave));
    }

    public ApiResponseDTO<Void> deleteEmployeeLeave(Long id) {
        employeeLeaveRepository.deleteById(id);
        return new ApiResponseDTO<>("Successfully Deleted Employee Leave", null);
    }

    public ApiResponseDTO<List<EmployeeRemainingLeaveDTO>> getRemainingLeaveCount(Long id, Long year) {
        List<EmployeeLeaveModel> employeeLeaves = employeeLeaveRepository.findByEmployeeIdAndStatusAndYear(id, EmployeeLeaveEnum.APPROVED, year);

        List<EmployeeRemainingLeaveDTO> employeeRemainingLeaveDTOs = new ArrayList<>();

        for (EmployeeLeaveModel leave : employeeLeaves) {
            LeavePolicyModel leavePolicy = leavePolicyService.getLeavePolicyModelById(leave.getLeavePolicy().getId());
            double remainingLeave = leavePolicy.getMaxDays() - leave.getNumberOfDays();

            employeeRemainingLeaveDTOs.add(new EmployeeRemainingLeaveDTO(
                    leavePolicy.getId(),
                    leavePolicy.getName(),
                    leavePolicy.getYear(),
                    leavePolicy.getLeaveType(),
                    leavePolicy.getMaxDays(),
                    remainingLeave)
            );
        }
        return new ApiResponseDTO<>("Successfully fetch Employee remaining Leave", employeeRemainingLeaveDTOs);
    }

    private EmployeeLeaveResponseDTO convertToResponseDTO(EmployeeLeaveModel employeeLeave) {
        return new EmployeeLeaveResponseDTO(
                employeeLeave.getId(),
                employeeLeave.getStartDate(),
                employeeLeave.getEndDate(),
                employeeLeave.getNumberOfDays(),
                employeeLeave.getStatus(),
                employeeLeave.getApprovedBy(),
                employeeLeave.getEmployee().getId(),
                employeeLeave.getLeavePolicy().getId()
        );
    }
}
