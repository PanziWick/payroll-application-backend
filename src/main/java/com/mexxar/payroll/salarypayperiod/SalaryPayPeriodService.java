package com.mexxar.payroll.salarypayperiod;

import com.mexxar.payroll.common.ApiResponseDTO;
import com.mexxar.payroll.salarypayperiod.exception.SalaryPayPeriodException;
import com.mexxar.payroll.salarypayperiod.exception.SalaryPayPeriodNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Service
public class SalaryPayPeriodService {
    private final SalaryPayPeriodRepository salaryPayPeriodRepository;

    public SalaryPayPeriodService(SalaryPayPeriodRepository salaryPayPeriodRepository) {
        this.salaryPayPeriodRepository = salaryPayPeriodRepository;
    }

    private static final Logger logger = LogManager.getLogger(SalaryPayPeriodService.class);

    private static final String PAY_PERIOD_NOT_FOUND = "PayPeriod not found with ID: ";

    public ApiResponseDTO<SalaryPayPeriodResponseDTO> createPayPeriod(SalaryPayPeriodRequestDTO request) {
        logger.debug("Starting to create Pay Period for: {}", request);

        Instant startTime = Instant.now();
        boolean exists = salaryPayPeriodRepository.existsByStartDateAndEndDateAndMonthOf(
                request.startDate(),
                request.endDate(),
                request.monthOf()
        );

        if (exists) {
            throw new SalaryPayPeriodException("A pay period with the same dates and month already exists.");
        }

        SalaryPayPeriodModel payPeriod = new SalaryPayPeriodModel();
        payPeriod.setStartDate(request.startDate());
        payPeriod.setEndDate(request.endDate());
        payPeriod.setMonthOf(request.monthOf());
        payPeriod.setYear(request.year());

        SalaryPayPeriodModel savedPayPeriod = salaryPayPeriodRepository.save(payPeriod);
        Instant endTime = Instant.now();
        Duration timeElapsed = Duration.between(startTime, endTime);
        logger.info("Pay Period created successfully for pay period id {} in {} ms", savedPayPeriod.getId(), timeElapsed.toMillis());

        return new ApiResponseDTO<>("Pay Period Created Successfully", convertToResponseDTO(savedPayPeriod));
    }

    public ApiResponseDTO<SalaryPayPeriodResponseDTO> getPayPeriodById(Long id) {
        logger.debug("Starting to get Pay Period for ID: {}", id);

        Instant startTime = Instant.now();
        SalaryPayPeriodModel payPeriod = salaryPayPeriodRepository.findById(id)
                .orElseThrow(() -> new SalaryPayPeriodNotFoundException(PAY_PERIOD_NOT_FOUND + id));
        Instant endTime = Instant.now();
        Duration timeElapsed = Duration.between(startTime, endTime);
        logger.info("Pay Period fetched successfully for ID {} in {} ms", id, timeElapsed.toMillis());

        return new ApiResponseDTO<>("Pay Period Fetched Successfully", convertToResponseDTO(payPeriod));
    }

    public SalaryPayPeriodModel getPayPeriodModelById(Long id) {
        logger.debug("Starting to get Pay Period model for ID: {}", id);

        return salaryPayPeriodRepository.findById(id)
                .orElseThrow(() -> new SalaryPayPeriodNotFoundException("Pay Period not found with ID: " + id));
    }

    public ApiResponseDTO<List<SalaryPayPeriodResponseDTO>> getAllPayPeriods() {
        logger.info("Fetching all Pay Periods");

        Instant startTime = Instant.now();
        List<SalaryPayPeriodResponseDTO> payPeriods = salaryPayPeriodRepository.findAll().stream()
                .map(this::convertToResponseDTO)
                .toList();
        Instant endTime = Instant.now();
        Duration timeElapsed = Duration.between(startTime, endTime);
        logger.info("Successfully fetched {} Pay Periods in {} ms", payPeriods.size(), timeElapsed.toMillis());

        return new ApiResponseDTO<>("Successfully Fetched All Pay Periods", payPeriods);
    }

    public ApiResponseDTO<SalaryPayPeriodResponseDTO> updatePayPeriod(Long id, SalaryPayPeriodRequestDTO request) {
        logger.debug("Starting to update Pay Period for ID: {} with data: {}", id, request);

        Instant startTime = Instant.now();
        SalaryPayPeriodModel existingPayPeriod = salaryPayPeriodRepository.findById(id)
                .orElseThrow(() -> new SalaryPayPeriodNotFoundException("Pay Period not found with ID: " + id));

        boolean exists = salaryPayPeriodRepository.existsByStartDateAndEndDateAndMonthOf(
                request.startDate(),
                request.endDate(),
                request.monthOf()
        );

        if (exists && !existingPayPeriod.getId().equals(id)) {
            throw new SalaryPayPeriodException("Another pay period with the same dates and month already exists.");
        }

        existingPayPeriod.setStartDate(request.startDate());
        existingPayPeriod.setEndDate(request.endDate());
        existingPayPeriod.setMonthOf(request.monthOf());
        existingPayPeriod.setYear(request.year());

        SalaryPayPeriodModel updatedPayPeriod = salaryPayPeriodRepository.save(existingPayPeriod);
        Instant endTime = Instant.now();
        Duration timeElapsed = Duration.between(startTime, endTime);
        logger.info("Pay Period updated successfully for ID {} in {} ms", id, timeElapsed.toMillis());

        return new ApiResponseDTO<>("Pay Period Updated Successfully", convertToResponseDTO(updatedPayPeriod));
    }

    public ApiResponseDTO<Void> deletePayPeriod(Long id) {
        logger.info("Starting to delete Pay Period for ID: {}", id);

        SalaryPayPeriodModel salaryPayPeriod = salaryPayPeriodRepository.findById(id)
                .orElseThrow(() -> new SalaryPayPeriodNotFoundException(PAY_PERIOD_NOT_FOUND + id));
        Instant startTime = Instant.now();
        salaryPayPeriodRepository.delete(salaryPayPeriod);
        Instant endTime = Instant.now();
        Duration timeElapsed = Duration.between(startTime, endTime);
        logger.info("Pay Period deleted successfully for ID {} in {} ms", id, timeElapsed.toMillis());

        return new ApiResponseDTO<>("Pay Period Deleted Successfully", null);
    }

    public List<SalaryPayPeriodResponseDTO> getAllPayPeriodByYear(Long year) {
        logger.info("Fetching all Pay Periods by year");

        Instant startTime = Instant.now();
        List<SalaryPayPeriodResponseDTO> payPeriods = salaryPayPeriodRepository.findAllByYear(year)
                .stream()
                .map(this::convertToResponseDTO)
                .toList();

        Instant endTime = Instant.now();
        Duration timeElapsed = Duration.between(startTime, endTime);
        logger.info("Successfully fetched all {} Pay Periods by year in {} ms", payPeriods.size(), timeElapsed.toMillis());

        return payPeriods;
    }

    public SalaryPayPeriodResponseDTO convertToResponseDTO(SalaryPayPeriodModel payPeriod) {
        return new SalaryPayPeriodResponseDTO(
                payPeriod.getId(),
                payPeriod.getStartDate(),
                payPeriod.getEndDate(),
                payPeriod.getMonthOf(),
                payPeriod.getYear()
        );
    }
}
