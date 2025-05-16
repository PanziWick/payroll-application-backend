package com.mexxar.payroll.address;

import com.mexxar.payroll.address.enums.TypeEnum;
import com.mexxar.payroll.address.exception.AddressNotFoundException;
import com.mexxar.payroll.common.ApiResponseDTO;
import com.mexxar.payroll.employee.EmployeeService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;

@Service
public class AddressService {

    private final AddressRepository addressRepository;
    private final EmployeeService employeeService;

    public AddressService(AddressRepository addressRepository, EmployeeService employeeService) {
        this.addressRepository = addressRepository;
        this.employeeService = employeeService;
    }

    private static final Logger logger = LogManager.getLogger(AddressService.class);

    private static final String ADDRESS_NOT_FOUND_MSG = "Address not found with id: ";

    @Transactional
    public ApiResponseDTO<AddressResponseDTO> createAddress(AddressRequestDTO addressRequestDTO) {
        if (addressRequestDTO == null) {
            logger.error("Attempted to create an address with a null request");
            throw new NullPointerException("Address request must not be null");
        }
        logger.info("Starting to create address for : {}", addressRequestDTO);

        AddressModel address = new AddressModel();
        address.setType(TypeEnum.valueOf(String.valueOf(addressRequestDTO.type())));
        address.setAddressLine1(addressRequestDTO.addressLine1());
        address.setAddressLine2(addressRequestDTO.addressLine2());
        address.setCity(addressRequestDTO.city());
        address.setPostalCode(addressRequestDTO.postalCode());
        address.setEmployee(employeeService.getEmployeeModelById(addressRequestDTO.employeeId()));

        Instant start = Instant.now();
        addressRepository.save(address);
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        logger.info("Address created successfully for address id {} in {} ms", address.getId(), timeElapsed.toMillis());

        return new ApiResponseDTO<>("Address created successfully", convertToResponseDTO(address));
    }

    public ApiResponseDTO<AddressResponseDTO> getAddressById(Long id) {
        logger.info("Starting to get address by ID for : {}", id);

        Instant start = Instant.now();
        AddressModel address = addressRepository.findById(id)
                .orElseThrow(() -> new AddressNotFoundException(ADDRESS_NOT_FOUND_MSG + id));
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        logger.info("Address fetched successfully for address id {} in {} ms", address.getId(), timeElapsed.toMillis());

        return new ApiResponseDTO<>("Address Fetched Successfully", convertToResponseDTO(address));
    }

    public ApiResponseDTO<Page<AddressResponseDTO>> getAllAddresses(int page, int size) {
        logger.info("Fetching all addresses for page {} with size {}.", page, size);

        Pageable pageable = PageRequest.of(page, size);
        Instant start = Instant.now();
        Page<AddressModel> addresses = addressRepository.findAll(pageable);
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        logger.info("Successfully fetched {} addresses in {} ms.", addresses.getTotalElements(), timeElapsed.toMillis());

        return new ApiResponseDTO<>("Successfully Fetched All Addresses", addresses.map(this::convertToResponseDTO));
    }

    @Transactional
    public ApiResponseDTO<AddressResponseDTO> updateAddress(Long id, AddressRequestDTO addressRequestDTO) {
        if (addressRequestDTO == null) {
            logger.error("Attempted to update an address with a null request");
            throw new NullPointerException("Address request must not be null");
        }
        logger.info("Starting to update address for : {}", addressRequestDTO);

        AddressModel address = addressRepository.findById(id)
                .orElseThrow(() -> new AddressNotFoundException(ADDRESS_NOT_FOUND_MSG + id));

        address.setType(TypeEnum.valueOf(String.valueOf(addressRequestDTO.type())));
        address.setAddressLine1(addressRequestDTO.addressLine1());
        address.setAddressLine2(addressRequestDTO.addressLine2());
        address.setCity(addressRequestDTO.city());
        address.setPostalCode(addressRequestDTO.postalCode());
        address.setEmployee(employeeService.getEmployeeModelById(addressRequestDTO.employeeId()));

        Instant start = Instant.now();
        addressRepository.save(address);
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        logger.info("Address updated successfully for address id {} in {} ms", address.getId(), timeElapsed.toMillis());

        return new ApiResponseDTO<>("Address Updated Successfully", convertToResponseDTO(address));
    }

    public ApiResponseDTO<Void> deleteAddress(Long id) {
        logger.info("Starting to delete address for id: {}", id);

        AddressModel address = addressRepository.findById(id)
                .orElseThrow(() -> new AddressNotFoundException("Address not found"));
        Instant start = Instant.now();
        addressRepository.delete(address);
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        logger.info("Address deleted successfully for address id {} in {} ms", id, timeElapsed.toMillis());

        return new ApiResponseDTO<>("Address Deleted Successfully", null);
    }

    private AddressResponseDTO convertToResponseDTO(AddressModel address) {
        return new AddressResponseDTO(
                address.getId(),
                address.getType(),
                address.getAddressLine1(),
                address.getAddressLine2(),
                address.getCity(),
                address.getPostalCode(),
                address.getEmployee().getId()
        );
    }
}
