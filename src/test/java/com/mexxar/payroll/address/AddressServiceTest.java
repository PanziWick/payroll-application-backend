package com.mexxar.payroll.address;

import com.mexxar.payroll.address.exception.AddressNotFoundException;
import com.mexxar.payroll.common.ApiResponseDTO;
import com.mexxar.payroll.employee.EmployeeModel;
import com.mexxar.payroll.employee.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.mexxar.payroll.address.enums.TypeEnum.PERMANENT;
import static com.mexxar.payroll.address.enums.TypeEnum.RESIDENTIAL;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    @InjectMocks
    private AddressService addressService;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private EmployeeService employeeService;

    private EmployeeModel employee;
    private AddressModel address1, address2;
    private AddressRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        employee = new EmployeeModel();
        employee.setId(1L);
        employee.setFirstName("Panzi");
        employee.setLastName("Wick");

        address1 = new AddressModel();
        address1.setId(1L);
        address1.setType(PERMANENT);
        address1.setAddressLine1("123");
        address1.setAddressLine2("ABC");
        address1.setCity("Central");
        address1.setPostalCode("0901");
        address1.setEmployee(employee);

        address2 = new AddressModel();
        address2.setId(2L);
        address2.setType(RESIDENTIAL);
        address2.setAddressLine1("0109");
        address2.setAddressLine2("XYZ");
        address2.setCity("Hill City");
        address2.setPostalCode("20800");
        address2.setEmployee(employee);

        requestDTO = new AddressRequestDTO(
                PERMANENT,
                "123",
                "ABC",
                "Central",
                "0901",
                1L
        );
    }

    @Test
    void should_successfully_create_address() {
        when(employeeService.getEmployeeModelById(1L)).thenReturn(employee);
        when(addressRepository.save(any(AddressModel.class))).thenReturn(address1);

        ApiResponseDTO<AddressResponseDTO> response = addressService.createAddress(requestDTO);

        assertNotNull(response);
        assertEquals("Address created successfully", response.getMessage());
        AddressResponseDTO responseDTO = response.getData(); // Assuming getData() retrieves the `AddressResponseDTO`
        assertNotNull(responseDTO);
        assertEquals(PERMANENT, responseDTO.type());
        assertEquals("123", responseDTO.addressLine1());
        assertEquals("ABC", responseDTO.addressLine2());
        assertEquals("Central", responseDTO.city());
        assertEquals("0901", responseDTO.postalCode());
        assertEquals(1L, responseDTO.employeeId());

        verify(addressRepository, times(1)).save(any(AddressModel.class));
    }

    @Test
    void should_throw_exception_when_creating_address_with_null_request() {
        AddressRequestDTO nullRequest = null;

        NullPointerException exception = assertThrows(NullPointerException.class, () ->
            addressService.createAddress(nullRequest)
        );

        assertEquals("Address request must not be null", exception.getMessage());
    }

    @Test
    void should_throw_exception_when_employee_not_found_for_address_creation() {
        when(employeeService.getEmployeeModelById(1L)).thenThrow(new RuntimeException("Employee not found"));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            addressService.createAddress(requestDTO)
        );

        assertEquals("Employee not found", exception.getMessage());
    }

    @Test
    void should_throw_exception_when_address_not_found() {
        when(addressRepository.findById(1L)).thenReturn(Optional.empty());

        AddressNotFoundException exception = assertThrows(AddressNotFoundException.class, () ->
            addressService.getAddressById(1L)
        );

        String expectedMessage = "Address not found";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void should_successfully_get_address_by_id() {
        when(addressRepository.findById(1L)).thenReturn(Optional.of(address1));

        ApiResponseDTO<AddressResponseDTO> response = addressService.getAddressById(1L);

        assertNotNull(response);
        assertEquals("Address Fetched Successfully", response.getMessage());
        AddressResponseDTO responseDTO = response.getData();
        assertEquals(PERMANENT, responseDTO.type());
        assertEquals("123", responseDTO.addressLine1());
        assertEquals("ABC", responseDTO.addressLine2());
        assertEquals("Central", responseDTO.city());
        assertEquals("0901", responseDTO.postalCode());
        assertEquals(1L, responseDTO.id());
        assertEquals(1L, responseDTO.employeeId());
    }

    @Test
    void should_successfully_get_all_addresses() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<AddressModel> addressPage = new PageImpl<>(Arrays.asList(address1, address2), pageable, 2);

        when(addressRepository.findAll(pageable)).thenReturn(addressPage);

        ApiResponseDTO<Page<AddressResponseDTO>> addressResponseDTOs = addressService.getAllAddresses(0, 10);

        assertNotNull(addressResponseDTOs);
        assertEquals(2, addressResponseDTOs.getData().getTotalElements());
        verify(addressRepository, times(1)).findAll(pageable);
    }

    @Test
    void should_return_empty_page_when_no_addresses_found() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<AddressModel> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        when(addressRepository.findAll(pageable)).thenReturn(emptyPage);

        ApiResponseDTO<Page<AddressResponseDTO>> addressResponseDTOs = addressService.getAllAddresses(0, 10);

        assertNotNull(addressResponseDTOs);
        assertEquals(0, addressResponseDTOs.getData().getTotalElements());
        verify(addressRepository, times(1)).findAll(pageable);
    }

    @Test
    void should_successfully_update_address() {
        when(addressRepository.findById(1L)).thenReturn(Optional.of(address1));
        when(employeeService.getEmployeeModelById(1L)).thenReturn(employee);
        when(addressRepository.save(any(AddressModel.class))).thenReturn(address1);

        ApiResponseDTO<AddressResponseDTO> response = addressService.updateAddress(1L, requestDTO);

        assertNotNull(response);
        assertEquals("Address Updated Successfully", response.getMessage());
        AddressResponseDTO responseDTO = response.getData();
        assertNotNull(responseDTO);
        assertEquals(PERMANENT, responseDTO.type());
        assertEquals("123", responseDTO.addressLine1());
        assertEquals("ABC", responseDTO.addressLine2());
        assertEquals("Central", responseDTO.city());
        assertEquals("0901", responseDTO.postalCode());
        assertEquals(1L, responseDTO.id());
        assertEquals(1L, responseDTO.employeeId());
    }

    @Test
    void should_throw_exception_when_updating_address_with_null_request() {
        AddressRequestDTO nullRequest = null;

        NullPointerException exception = assertThrows(NullPointerException.class, () ->
            addressService.updateAddress(1L, nullRequest)
        );

        assertEquals("Address request must not be null", exception.getMessage());
    }

    @Test
    void should_throw_exception_when_updating_with_nonexistent_employee() {
        when(addressRepository.findById(1L)).thenReturn(Optional.of(address1));
        when(employeeService.getEmployeeModelById(99L)).thenThrow(new RuntimeException("Employee not found"));

        requestDTO = new AddressRequestDTO(
                PERMANENT,
                "Updated Line1",
                "Updated Line2",
                "Updated City",
                "11111",
                99L
        );

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            addressService.updateAddress(1L, requestDTO)
        );

        assertEquals("Employee not found", exception.getMessage());
    }

    @Test
    void should_throw_exception_when_updating_with_nonexistent_address() {
        when(addressRepository.findById(10L)).thenReturn(Optional.empty());

        AddressNotFoundException exception = assertThrows(AddressNotFoundException.class, () ->
            addressService.updateAddress(10L, requestDTO)
        );

        assertEquals("Address not found with id: 10", exception.getMessage());
    }

    @Test
    void should_successfully_delete_address() {
        when(addressRepository.findById(1L)).thenReturn(Optional.of(address1));
        doNothing().when(addressRepository).delete(address1);

        addressService.deleteAddress(1L);

        verify(addressRepository, times(1)).findById(1L);
        verify(addressRepository, times(1)).delete(address1);
    }

    @Test
    void should_throw_exception_when_deleting_nonexistent_address() {
        when(addressRepository.findById(10L)).thenReturn(Optional.empty());

        AddressNotFoundException exception = assertThrows(
                AddressNotFoundException.class,
                () -> addressService.deleteAddress(10L)
        );

        String expectedMessage = "Address not found";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void should_verify_arguments_when_saving_address() {
        when(employeeService.getEmployeeModelById(1L)).thenReturn(employee);
        when(addressRepository.save(any(AddressModel.class))).thenReturn(address1);

        addressService.createAddress(requestDTO);

        ArgumentCaptor<AddressModel> addressCaptor = ArgumentCaptor.forClass(AddressModel.class);
        verify(addressRepository, times(1)).save(addressCaptor.capture());

        AddressModel capturedAddress = addressCaptor.getValue();
        assertEquals(PERMANENT, capturedAddress.getType());
        assertEquals("123", capturedAddress.getAddressLine1());
        assertEquals("ABC", capturedAddress.getAddressLine2());
        assertEquals("Central", capturedAddress.getCity());
        assertEquals("0901", capturedAddress.getPostalCode());
        assertEquals(employee, capturedAddress.getEmployee());
    }
}
