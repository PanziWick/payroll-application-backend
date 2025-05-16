package com.mexxar.payroll.tax;

import com.mexxar.payroll.common.ApiResponseDTO;
import com.mexxar.payroll.tax.exception.TaxNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Service
public class TaxService {

    private final TaxRepository taxRepository;

    public TaxService(TaxRepository taxRepository) {
        this.taxRepository = taxRepository;
    }

    private static final Logger logger = LogManager.getLogger(TaxService.class);

    private static final String TAX_NOT_FOUND_WITH_ID = "Tax not found with id: ";

    @Transactional
    public ApiResponseDTO<TaxResponseDTO> createTax(TaxRequestDTO taxRequestDTO) {
        logger.info("Starting to create tax for: {}", taxRequestDTO);

        TaxModel taxModel = new TaxModel();
        taxModel.setTaxRate(taxRequestDTO.taxRate());
        taxModel.setMinSalary(taxRequestDTO.minSalary());
        taxModel.setMaxSalary(taxRequestDTO.maxSalary());

        Instant start = Instant.now();
        taxRepository.save(taxModel);
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        logger.info("Tax created successfully with id {} in {} ms", taxModel.getId(), timeElapsed.toMillis());

        return new ApiResponseDTO<>("Tax Created Successfully", convertToResponseDTO(taxModel));
    }

    public ApiResponseDTO<TaxResponseDTO> getTaxById(Long id) {
        logger.info("Fetching tax by ID: {}", id);

        Instant start = Instant.now();
        TaxModel tax = taxRepository.findById(id)
                .orElseThrow(() -> new TaxNotFoundException(TAX_NOT_FOUND_WITH_ID + id));
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        logger.info("Tax fetched successfully for id {} in {} ms", tax.getId(), timeElapsed.toMillis());

        return new ApiResponseDTO<>("Tax Fetched Successfully", convertToResponseDTO(tax));
    }

    public ApiResponseDTO<List<TaxResponseDTO>> getTaxRateBySalaryRange(Double salary) {
        logger.info("Fetching tax rates for salary range including: {}", salary);

        Instant start = Instant.now();
        List<TaxModel> tax = taxRepository.findByMinSalaryLessThanEqual(salary);
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        logger.info("Successfully fetched tax rates for salary range in {} ms", timeElapsed.toMillis());

        List<TaxResponseDTO> responseDTOs = tax.stream()
                .map(this::convertToResponseDTO)
                .toList();

        return new ApiResponseDTO<>("Tax Fetched Successfully For Salary Range", responseDTOs);
    }

    public List<TaxResponseDTO> getTaxBySalaryRange(Double salary) {
        logger.info("Fetching tax for salary range including: {}", salary);

        Instant start = Instant.now();
        List<TaxModel> tax = taxRepository.findByMinSalaryLessThanEqual(salary);
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        logger.info("Successfully fetched tax for salary range in {} ms", timeElapsed.toMillis());

        return tax.stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    public ApiResponseDTO<List<TaxResponseDTO>> getAllTaxes() {
        logger.info("Fetching all taxes.");

        Instant start = Instant.now();
        List<TaxModel> taxes = taxRepository.findAll();
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        logger.info("Successfully fetched {} taxes in {} ms.", taxes.size(), timeElapsed.toMillis());

        List<TaxResponseDTO> responseDTOs = taxes.stream()
                .map(this::convertToResponseDTO)
                .toList();

        return new ApiResponseDTO<>("Successfully Fetched All Taxes", responseDTOs);
    }

    @Transactional
    public ApiResponseDTO<TaxResponseDTO> updateTax(Long id, TaxRequestDTO taxRequestDTO) {
        logger.info("Updating tax with id: {}", id);

        TaxModel tax = taxRepository.findById(id)
                .orElseThrow(() -> new TaxNotFoundException(TAX_NOT_FOUND_WITH_ID + id));
        tax.setTaxRate(taxRequestDTO.taxRate());
        tax.setMinSalary(taxRequestDTO.minSalary());
        tax.setMaxSalary(taxRequestDTO.maxSalary());

        Instant start = Instant.now();
        taxRepository.save(tax);
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        logger.info("Tax updated successfully for id {} in {} ms", id, timeElapsed.toMillis());

        return new ApiResponseDTO<>("Tax Updated Successfully", convertToResponseDTO(tax));
    }

    public ApiResponseDTO<Void> deleteTax(Long id) {
        logger.info("Deleting tax with id: {}", id);

        TaxModel tax = taxRepository.findById(id)
                .orElseThrow(() -> new TaxNotFoundException(TAX_NOT_FOUND_WITH_ID + id));
        Instant start = Instant.now();
        taxRepository.delete(tax);
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        logger.info("Tax deleted successfully for id {} in {} ms", id, timeElapsed.toMillis());

        return new ApiResponseDTO<>("Tax Deleted Successfully", null);
    }

    private TaxResponseDTO convertToResponseDTO(TaxModel tax) {
        return new TaxResponseDTO(
                tax.getId(),
                tax.getTaxRate(),
                tax.getMinSalary(),
                tax.getMaxSalary()
        );
    }
}
