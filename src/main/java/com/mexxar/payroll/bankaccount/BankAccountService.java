package com.mexxar.payroll.bankaccount;

import com.mexxar.payroll.bankaccount.exception.BankAccountException;
import com.mexxar.payroll.bankaccount.exception.BankAccountNotFoundException;
import com.mexxar.payroll.common.ApiResponseDTO;
import com.mexxar.payroll.employee.EmployeeModel;
import com.mexxar.payroll.employee.EmployeeService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BankAccountService {
    private final BankAccountRepository bankAccountRepository;
    private final EmployeeService employeeService;

    private static final Logger logger = LogManager.getLogger(BankAccountService.class);

    private static final String DUPLICATE_ACCOUNT_NUMBER_MSG = "Account number already exists for this employee";
    private static final String BANK_ACCOUNT_NOT_FOUND_MSG = "Bank account not found with id: ";

    @Transactional
    public ApiResponseDTO<BankAccountResponseDTO> createBankAccount(Long employeeId, BankAccountRequestDTO request) {
        logger.debug("Starting to create bank account for employee id: {}", employeeId);

        EmployeeModel employee = employeeService.getEmployeeModelById(employeeId);

        if (bankAccountRepository.existsByEmployeeAndAccountNumber(employee, request.accountNumber())) {
            throw new BankAccountException(DUPLICATE_ACCOUNT_NUMBER_MSG);
        }

        if (request.accountType() == AccountTypeEnum.PRIMARY) {
            List<BankAccountModel> employeePrimaryAccounts = bankAccountRepository.findByEmployeeAndAccountType(employee, AccountTypeEnum.PRIMARY);
            employeePrimaryAccounts.forEach(bankAccount -> {
                bankAccount.setAccountType(AccountTypeEnum.SECONDARY);
                bankAccountRepository.save(bankAccount);
            });
        }

        BankAccountModel bankAccount = new BankAccountModel();
        bankAccount.setAccountHolderName(request.accountHolderName());
        bankAccount.setBankName(request.bankName());
        bankAccount.setAccountNumber(request.accountNumber());
        bankAccount.setBranchName(request.branchName());
        bankAccount.setAccountType(request.accountType());
        bankAccount.setEmployee(employee);

        Instant start = Instant.now();
        BankAccountModel savedAccount = bankAccountRepository.save(bankAccount);
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        logger.info("Bank account created successfully for employee id {} in {} ms", employeeId, timeElapsed.toMillis());

        return new ApiResponseDTO<>("Bank Account Created Successfully", convertToResponseDTO(savedAccount));
    }

    public ApiResponseDTO<BankAccountResponseDTO> getBankAccountById(Long id) {
        logger.info("Starting to get bank account by id: {}", id);

        return new ApiResponseDTO<>("Bank Account Fetched Successfully", convertToResponseDTO(findBankAccountById(id)));
    }

    private BankAccountModel findBankAccountById(Long id) {
        logger.info("Starting to find bank account for id: {}", id);

        return bankAccountRepository.findById(id)
                .orElseThrow(() -> new BankAccountNotFoundException(BANK_ACCOUNT_NOT_FOUND_MSG + id));
    }

    public ApiResponseDTO<Page<BankAccountResponseDTO>> getAllBankAccounts(int page, int size) {
        logger.info("Fetching all bank accounts for page {} with size {}.", page, size);

        Pageable pageable = PageRequest.of(page, size);

        Instant start = Instant.now();
        Page<BankAccountModel> bankAccounts = bankAccountRepository.findAll(pageable);
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        logger.info("Successfully fetched all {} bank accounts in {} ms.", bankAccounts.getTotalElements(), timeElapsed.toMillis());

        return new ApiResponseDTO<>("Successfully Fetched All Bank Accounts", bankAccounts.map(this::convertToResponseDTO));
    }

    @Transactional
    public ApiResponseDTO<BankAccountResponseDTO> updateBankAccount(Long id, BankAccountRequestDTO request) {
        logger.info("Starting to update bank account for id: {}", id);

        BankAccountModel bankAccount = findBankAccountById(id);

        if (bankAccountRepository.existsByEmployeeAndAccountNumber(bankAccount.getEmployee(), request.accountNumber())) {
            throw new BankAccountException(DUPLICATE_ACCOUNT_NUMBER_MSG);
        }

        if (request.accountType() == AccountTypeEnum.PRIMARY) {
            List<BankAccountModel> employeePrimaryAccounts = bankAccountRepository.findByEmployeeAndAccountType(bankAccount.getEmployee(), AccountTypeEnum.PRIMARY);
            employeePrimaryAccounts.forEach(existingAccount -> {
                existingAccount.setAccountType(AccountTypeEnum.SECONDARY);
                bankAccountRepository.save(existingAccount);
            });
        }

        bankAccount.setAccountHolderName(request.accountHolderName());
        bankAccount.setBankName(request.bankName());
        bankAccount.setAccountNumber(request.accountNumber());
        bankAccount.setBranchName(request.branchName());
        bankAccount.setAccountType(request.accountType());

        Instant start = Instant.now();
        BankAccountModel updatedAccount = bankAccountRepository.save(bankAccount);
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        logger.info("Bank account updated successfully for id {} in {} ms", id, timeElapsed.toMillis());

        return new ApiResponseDTO<>("Bank Account Updated Successfully", convertToResponseDTO(updatedAccount));
    }

    @Transactional
    public ApiResponseDTO<BankAccountResponseDTO> changeAccountType(Long id, AccountTypeEnum newAccountType) {
        logger.info("Starting to change account type for bank account id: {}", id);

        Instant start = Instant.now();

        if (newAccountType == AccountTypeEnum.PRIMARY) {
            List<BankAccountModel> employeeBankAccounts = bankAccountRepository.findByEmployeeAndAccountType(findBankAccountById(id).getEmployee(), newAccountType);
            employeeBankAccounts.forEach(bankAccount -> {
                bankAccount.setAccountType(AccountTypeEnum.SECONDARY);
                bankAccountRepository.save(bankAccount);
            });
        }

        BankAccountModel bankAccount = findBankAccountById(id);
        bankAccount.setAccountType(newAccountType);
        BankAccountModel updatedAccount = bankAccountRepository.save(bankAccount);

        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        logger.info("Account type changed successfully for bank account id {} in {} ms", id, timeElapsed.toMillis());

        return new ApiResponseDTO<>("Account Type Changed Successfully", convertToResponseDTO(updatedAccount));
    }

    public void deleteBankAccount(Long id) {
        logger.info("Starting to delete bank account for id: {}", id);

        BankAccountModel bankAccount = findBankAccountById(id);

        Instant start = Instant.now();
        bankAccountRepository.delete(bankAccount);
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        logger.info("Bank account deleted successfully for id {} in {} ms", id, timeElapsed.toMillis());
    }

    private BankAccountResponseDTO convertToResponseDTO(BankAccountModel bankAccount) {
        return new BankAccountResponseDTO(
                bankAccount.getId(),
                bankAccount.getAccountHolderName(),
                bankAccount.getBankName(),
                bankAccount.getAccountNumber(),
                bankAccount.getBranchName(),
                bankAccount.getAccountType(),
                bankAccount.getEmployee()
        );
    }
}
