package com.mexxar.payroll.epfetfcontribution;

import com.mexxar.payroll.common.ApiResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/epf-etf-contributions")
public class EpfEtfContributionController {

    private final EpfEtfContributionService service;

    public EpfEtfContributionController(EpfEtfContributionService service) {
        this.service = service;
    }

    private static final Logger logger = LogManager.getLogger(EpfEtfContributionController.class);

    @Operation(summary = "Get contributions by employee ID", description = "Fetches all EPF/ETF contributions for a given employee ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved contributions"),
            @ApiResponse(responseCode = "404", description = "No contributions found for the given employee ID")
    })
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<ApiResponseDTO<List<EpfEtfContributionResponseDTO>>> getContributionsByEmployeeId(@PathVariable Long employeeId) {
        logger.info("Received request to fetch contributions for employee ID: {}", employeeId);
        ApiResponseDTO<List<EpfEtfContributionResponseDTO>> contributions = service.getEpfEtfContributionsByEmployeeId(employeeId);
        return ResponseEntity.ok(contributions);
    }

    @Operation(summary = "Get contributions by month", description = "Fetches all EPF/ETF contributions for a specified month.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved contributions"),
            @ApiResponse(responseCode = "404", description = "No contributions found for the specified month")
    })
    @GetMapping("/month")
    public ResponseEntity<ApiResponseDTO<List<EpfEtfContributionResponseDTO>>> getContributionsByMonth(@RequestParam Long payPeriodId) {
        logger.info("Received request to fetch contributions for the salary pay period: {}", payPeriodId);
        ApiResponseDTO<List<EpfEtfContributionResponseDTO>> contributions = service.getEpfEtfContributionsByMonthOf(payPeriodId);
        return ResponseEntity.ok(contributions);
    }

    @Operation(summary = "Get contribution by ID", description = "Fetches an EPF/ETF contribution by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the contribution"),
            @ApiResponse(responseCode = "404", description = "Contribution not found for the given ID")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<EpfEtfContributionResponseDTO>> getContributionById(@PathVariable Long id) {
        logger.info("Received request to fetch contribution by ID: {}", id);
        ApiResponseDTO<EpfEtfContributionResponseDTO> contribution = service.getContributionById(id);
        return ResponseEntity.ok(contribution);
    }

    @Operation(summary = "Get all contributions", description = "Fetches all EPF/ETF contributions.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved all contributions"),
            @ApiResponse(responseCode = "204", description = "No contributions available")
    })
    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<EpfEtfContributionResponseDTO>>> getAllContributions() {
        logger.info("Received request to fetch all contributions");
        ApiResponseDTO<List<EpfEtfContributionResponseDTO>> contributions = service.getAllContributions();

        if (contributions.getData().isEmpty()) {
            logger.warn("No contributions found");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return ResponseEntity.ok(contributions);
        }
    }
}
