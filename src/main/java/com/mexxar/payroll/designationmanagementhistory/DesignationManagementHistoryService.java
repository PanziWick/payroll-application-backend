package com.mexxar.payroll.designationmanagementhistory;

import com.mexxar.payroll.employee.EmployeeModel;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class DesignationManagementHistoryService {

    private final DesignationManagementHistoryRepository historyRepository;

    public void logDesignationChange(
            EmployeeModel employee,
            Long previousDesignationId,
            Long currentDesignationId,
            Long previousDepartmentId,
            Long currentDepartmentId) {
        DesignationManagementHistoryModel history = new DesignationManagementHistoryModel();
        history.setEmployee(employee);
        history.setPreviousDesignationId(previousDesignationId);
        history.setCurrentDesignationId(currentDesignationId);
        history.setPreviousDepartmentId(previousDepartmentId);
        history.setCurrentDepartmentId(currentDepartmentId);
        history.setChangeDate(new Date());
        history.setChangedBy(getLoggedInUsername());

        historyRepository.save(history);
    }

    private String getLoggedInUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else {
            return principal.toString();
        }
    }
}
