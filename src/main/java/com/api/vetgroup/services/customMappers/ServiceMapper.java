package com.api.vetgroup.services.customMappers;

import com.api.vetgroup.dtos.ServiceCreateDto;
import com.api.vetgroup.dtos.response.ServiceResponseDto;
import com.api.vetgroup.dtos.StaffReducedDto;
import com.api.vetgroup.dtos.response.PatientResponseDto;
import com.api.vetgroup.models.Patient;
import com.api.vetgroup.models.StaffUser;
import com.api.vetgroup.models.VetService;
import com.api.vetgroup.models.enums.ServiceStatus;
import com.api.vetgroup.models.enums.ServiceTypes;
import com.api.vetgroup.services.PatientService;
import com.api.vetgroup.services.StaffUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServiceMapper {

    @Autowired
    private StaffMapper staffMapper;

    @Autowired
    private PatientMapper patientMapper;

    @Autowired
    private StaffUserService staffService;

    @Autowired
    private PatientService patientService;

    public ServiceResponseDto convertServiceToDto(VetService service) {
        try {
            ServiceResponseDto serviceDto = new ServiceResponseDto();
            BeanUtils.copyProperties(service, serviceDto);

            StaffReducedDto staff = staffMapper.convertStaffToReducedDto(service.getStaff());
            PatientResponseDto patient = patientMapper.convertPatientToDto(service.getPatient());

            serviceDto.setStaff(staff);
            serviceDto.setPatient(patient);

            return serviceDto;
        } catch (Exception e) {
            throw new RuntimeException("Error during conversion to ServiceDto");
        }
    }

    public VetService convertDtoToService(ServiceCreateDto serviceDto) {
        try {
            VetService service = new VetService();
            BeanUtils.copyProperties(serviceDto, service);

            StaffUser staff = staffService.findById(serviceDto.getStaff_id());
            Patient patient = patientService.findById(serviceDto.getPatient_id());

            service.setType(serviceDto.getType());
            service.setStaff(staff);
            service.setPatient(patient);
            service.setCity(serviceDto.getCity());
            service.setStatus(serviceDto.getStatus());

            if (serviceDto.getType() == ServiceTypes.EMERGENCY) {
                service.setStatus(ServiceStatus.WAITING_PAYMENT);
            }

            return service;
        } catch (Exception e) {
            throw new RuntimeException("Error during conversion to Service");
        }
    }

}
