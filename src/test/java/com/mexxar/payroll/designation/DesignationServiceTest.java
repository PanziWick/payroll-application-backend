package com.mexxar.payroll.designation;

import com.mexxar.payroll.common.ApiResponseDTO;
import com.mexxar.payroll.common.enums.StatusEnum;
import com.mexxar.payroll.designation.exception.DesignationException;
import com.mexxar.payroll.designation.exception.DesignationNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Optional;

import static com.mexxar.payroll.common.enums.StatusEnum.ACTIVE;
import static com.mexxar.payroll.common.enums.StatusEnum.INACTIVE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DesignationServiceTest {

    @InjectMocks
    private DesignationService designationService;

    @Mock
    private DesignationRepository designationRepository;

    DesignationModel designation1, designation2, designation3;

    DesignationRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        designation1 = new DesignationModel();
        designation1.setId(1L);
        designation1.setJobTitle("Developer");
        designation1.setJobDescription("Develops software");
        designation1.setStatus(ACTIVE);

        designation2 = new DesignationModel();
        designation2.setId(2L);
        designation2.setJobTitle("Tester");
        designation2.setJobDescription("Tests software");
        designation2.setStatus(ACTIVE);

        designation3 = new DesignationModel();
        designation3.setId(3L);
        designation3.setJobTitle("QA");
        designation3.setJobDescription("QA software");
        designation3.setStatus(INACTIVE);

        requestDTO = new DesignationRequestDTO(
                "Developer",
                "Develops software",
                StatusEnum.ACTIVE
        );
    }

    @Test
    void should_successfully_create_a_designation() {
        when(designationRepository.save(any(DesignationModel.class))).thenReturn(designation1);

        ApiResponseDTO<DesignationResponseDTO> response = designationService.createDesignation(requestDTO);

        assertNotNull(response);
        assertEquals("Designation Created Successfully", response.getMessage());        DesignationResponseDTO responseDTO = response.getData();
        assertNotNull(responseDTO);
        assertAll(
                () -> assertNotNull(responseDTO),
                () -> assertEquals(1L, responseDTO.id()),
                () -> assertEquals("Developer", responseDTO.jobTitle()),
                () -> assertEquals("Develops software", responseDTO.jobDescription()),
                () -> assertEquals(ACTIVE, responseDTO.status())
        );
        verify(designationRepository, times(1)).save(any(DesignationModel.class));
    }

    @Test
    void should_successfully_not_create_duplicate_designation() {
        when(designationRepository.findByJobTitleAndJobDescription(anyString(), anyString())).thenReturn(Optional.of(designation1));

        DesignationModel designation = new DesignationModel();
        designation.setJobTitle("Developer");
        designation.setJobDescription("Develops software");
        designation.setStatus(ACTIVE);

        assertThrows(DesignationException.class, () -> designationService.createDesignation(requestDTO));
        verify(designationRepository, never()).save(any(DesignationModel.class));
    }

    @Test
    void should_throw_exception_when_createDesignation_request_is_null() {
        assertThrows(IllegalArgumentException.class, () -> designationService.createDesignation(null));
    }

    @Test
    void should_successfully_get_designation_by_id() {
        when(designationRepository.findById(1L)).thenReturn(Optional.of(designation1));

        ApiResponseDTO<DesignationResponseDTO> response = designationService.getDesignationById(1L);

        assertNotNull(response);
        assertEquals("Designation Fetched Successfully", response.getMessage());
        DesignationResponseDTO responseDTO = response.getData();
        assertEquals(1L, responseDTO.id());
        assertEquals("Developer", responseDTO.jobTitle());
        assertEquals("Develops software", responseDTO.jobDescription());
        assertEquals(ACTIVE, responseDTO.status());
        verify(designationRepository, times(1)).findById(1L);
    }

    @Test
    void should_successfully_get_designation_id_not_found() {
        when(designationRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(DesignationNotFoundException.class, () -> designationService.getDesignationById(1L));
        verify(designationRepository, times(1)).findById(1L);
    }

    @Test
    void should_successfully_get_FindById() {
        when(designationRepository.findById(1L)).thenReturn(Optional.of(designation1));

        DesignationModel foundDesignation = designationService.findDesignationById(1L);

        assertNotNull(foundDesignation);
        assertEquals(designation1.getId(), foundDesignation.getId());
        assertEquals(designation1.getJobTitle(), foundDesignation.getJobTitle());
        assertEquals(designation1.getJobDescription(), foundDesignation.getJobDescription());
        assertEquals(designation1.getStatus(), foundDesignation.getStatus());
    }

    @Test
    void should_successfully_get_FindById_NotFound() {
        when(designationRepository.findById(1L)).thenReturn(Optional.empty());

        DesignationNotFoundException exception = assertThrows(DesignationNotFoundException.class,
                () -> designationService.findDesignationById(1L)
        );

        assertEquals("Designation not found with id: " + 1L, exception.getMessage());
    }

    @Test
    void should_successfully_get_all_designations() {
        Pageable pageable = PageRequest.of(0,10);

        Page<DesignationModel> designationPage = new PageImpl<>(Arrays.asList(designation1, designation2, designation3), pageable, 3);

        when(designationRepository.findAll(pageable)).thenReturn(designationPage);

        ApiResponseDTO<Page<DesignationResponseDTO>> designationResponseDTOS = designationService.getAllDesignations(0, 10);

        assertNotNull(designationResponseDTOS);
        assertEquals(3, designationResponseDTOS.getData().getTotalElements());
        verify(designationRepository, times(1)).findAll(pageable);
    }

    @Test
    void should_successfully_get_all_active_designations() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<DesignationModel> designationPage = new PageImpl<>(Arrays.asList(designation1, designation2), pageable, 2);

        when(designationRepository.findByStatus(ACTIVE, pageable)).thenReturn(designationPage);

        ApiResponseDTO<Page<DesignationResponseDTO>> activeDesignations = designationService.getAllActiveDesignations(0, 10);

        assertNotNull(activeDesignations);
        assertEquals(2, activeDesignations.getData().getTotalElements());
        verify(designationRepository, times(1)).findByStatus(ACTIVE, pageable);
    }

    @Test
    void should_successfully_update_the_designation() {
        requestDTO = new DesignationRequestDTO(
                "Senior Developer",
                "Develops and designs software",
                StatusEnum.ACTIVE
        );

        when(designationRepository.findById(1L))
                .thenReturn(Optional.of(designation1));
        when(designationRepository.save(any(DesignationModel.class)))
                .thenReturn(designation1);


        ApiResponseDTO<DesignationResponseDTO> response = designationService.updateDesignation(1L, requestDTO);

        assertNotNull(response);
        assertEquals("Designation Updated Successfully", response.getMessage());        DesignationResponseDTO responseDTO = response.getData();
        assertNotNull(responseDTO);
        assertEquals(1L, responseDTO.id());
        assertEquals("Senior Developer", responseDTO.jobTitle());
        assertEquals("Develops and designs software", responseDTO.jobDescription());
        assertEquals(ACTIVE, responseDTO.status());
        verify(designationRepository, times(1)).findById(1L);
        verify(designationRepository, times(1)).save(any(DesignationModel.class));
    }

    @Test
    void should_throw_exception_when_updateDesignation_request_is_null() {
        assertThrows(IllegalArgumentException.class, () -> designationService.updateDesignation(1L, null));
        verify(designationRepository, never()).save(any(DesignationModel.class));
    }

    @Test
    void should_successfully_update_designation_status() {
        DesignationModel designation = new DesignationModel();
        designation.setId(1L);
        designation.setJobTitle("Developer");
        designation.setJobDescription("Develops software");
        designation.setStatus(INACTIVE);

        when(designationRepository.findById(1L)).thenReturn(Optional.of(designation));
        when(designationRepository.save(designation)).thenReturn(designation);

        ApiResponseDTO<DesignationResponseDTO> response = designationService.updateDesignationStatus(1L, String.valueOf(ACTIVE));

        assertNotNull(response);
        assertEquals("Designation Status Updated Successfully", response.getMessage());
        DesignationResponseDTO responseDTO = response.getData();
        assertNotNull(responseDTO);
        assertEquals(ACTIVE, responseDTO.status());
        verify(designationRepository, times(1)).findById(1L);
        verify(designationRepository, times(1)).save(designation);
    }

    @Test
    void should_successfully_delete_the_designation() {
        when(designationRepository.findById(1L))
                .thenReturn(Optional.of(designation1));

        designationService.deleteDesignation(1L);

        verify(designationRepository, times(1)).findById(1L);
        assertEquals(INACTIVE, designation1.getStatus());
    }
}