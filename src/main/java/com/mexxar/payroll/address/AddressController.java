package com.mexxar.payroll.address;

import com.mexxar.payroll.common.ApiResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/addresses")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    private static final Logger logger = LogManager.getLogger(AddressController.class);

    @Operation(summary = "Create a new address", description = "This endpoint creates a new address.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created the address"),
            @ApiResponse(responseCode = "400", description = "Invalid input, object invalid")
    })
    @PostMapping
    public ResponseEntity<ApiResponseDTO<AddressResponseDTO>> createAddress(@Valid @RequestBody AddressRequestDTO addressRequestDTO) {
        logger.info("Received request to create an Address");
        ApiResponseDTO<AddressResponseDTO> addressResponseDTO = addressService.createAddress(addressRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(addressResponseDTO);
    }

    @Operation(summary = "Get address by ID", description = "This endpoint returns an address by its given ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the address"),
            @ApiResponse(responseCode = "404", description = "Address not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<AddressResponseDTO>> getAddressById(@PathVariable Long id) {
        logger.info("Received request to get Address by ID");
        ApiResponseDTO<AddressResponseDTO> addressResponseDTO = addressService.getAddressById(id);
        return ResponseEntity.ok(addressResponseDTO);
    }

    @Operation(summary = "Get all address", description = "This endpoint returns a list of all address.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved address list"),
            @ApiResponse(responseCode = "204", description = "No content, no address found")
    })
    @GetMapping
    public ResponseEntity<ApiResponseDTO<Page<AddressResponseDTO>>> getAllAddresses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        logger.info("Received request to get all Addresses");
        ApiResponseDTO<Page<AddressResponseDTO>> allAddresses = addressService.getAllAddresses(page, size);

        if (allAddresses.getData().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return ResponseEntity.ok(allAddresses);
        }
    }

    @Operation(summary = "Update an address", description = "This endpoint updates an existing address.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated the address"),
            @ApiResponse(responseCode = "400", description = "Invalid input, object invalid"),
            @ApiResponse(responseCode = "404", description = "Address not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<AddressResponseDTO>> updateAddress(@PathVariable Long id, @Valid @RequestBody AddressRequestDTO addressRequestDTO) {
        logger.info("Received request to update an Address");
        ApiResponseDTO<AddressResponseDTO> updatedAddress = addressService.updateAddress(id, addressRequestDTO);
        return ResponseEntity.ok(updatedAddress);
    }

    @Operation(summary = "Delete a address", description = "This endpoint deletes an address")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted the address"),
            @ApiResponse(responseCode = "404", description = "Address not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<Void>> deleteAddress(@PathVariable Long id) {
        logger.info("Received request to delete an Address");
        ApiResponseDTO<Void> response = addressService.deleteAddress(id);
        return ResponseEntity.ok(response);
    }
}
