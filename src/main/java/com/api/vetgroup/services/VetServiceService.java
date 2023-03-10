package com.api.vetgroup.services;

import com.api.vetgroup.models.StaffUser;
import com.api.vetgroup.models.VetService;
import com.api.vetgroup.models.enums.ServiceStatus;
import com.api.vetgroup.models.enums.ServiceTypes;
import com.api.vetgroup.repositories.ServiceRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class VetServiceService {

    @Autowired
    private ServiceRepository repository;

    @Autowired
    private StaffUserService staffService;

    public List<VetService> findAll(String sort_by, String direction) {
        var dir = Objects.equals(direction.toUpperCase(), "ASC") ? Sort.Direction.ASC : Sort.Direction.DESC;
        return  repository.findAll(Sort.by(dir, sort_by));
    }

    public VetService findById(Long id) {
        Optional<VetService> obj = repository.findById(id);
        return obj.get();
    }

    @Transactional
    public VetService insert(VetService newService) {
        var staff = staffService.findById(newService.getStaff().getId());

        if (!staff.getOnDuty() && newService.getType() != ServiceTypes.EXAM) {
            throw new IllegalArgumentException("This staff is not on duty");
        }

        if (newService.getStatus() != ServiceStatus.SCHEDULED) {
            newService.setServiceDate(null);
        }

        return repository.save(newService);
    }

    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Transactional
    public void update(VetService service) {
        repository.save(service);
    }

    public List<VetService> findServicesByPatientId(Long id) {
        List<VetService> list = repository.findServicesByPatientId(id);

        return list;
    }

    public List<VetService> findServicesByStaffId(Long id) {
        List<VetService> list = repository.findServiceByStaffId(id);
        return list;
    }

    public void updateDescription(Long id, String authorization, String description) {
        VetService service = findById(id);
        StaffUser staff = staffService.findByJwt(authorization);

        if (service.getStaff().getId() != staff.getId()) {
            throw new IllegalArgumentException("You don't have permission to update this description");
        }

        if (Objects.equals(service.getDescription(), description)) {
            throw new IllegalArgumentException("The update data provided is the same as the old one");
        }

        service.setDescription(description);
        update(service);
    }

    @Transactional
    public void changeStatus(Long id, ServiceStatus status, String authorization) throws IllegalAccessException {
        VetService service = findById(id);
        StaffUser staffCreator = staffService.findByJwt(authorization);

        if (service.getStaff().getId() != staffCreator.getId()) {
            throw new IllegalArgumentException("You don't have permission");
        }

        if (!staffCreator.getOnDuty())  {
            throw new IllegalArgumentException("You aren't on duty");
        }

        if (service.getType() == ServiceTypes.EMERGENCY && status == ServiceStatus.SCHEDULED) {
            throw new IllegalAccessException("Service of EMERGENCY not accept the status SCHEDULED");
        }

        if (service.getStatus() == status) {
            throw new IllegalArgumentException("This service is already "+ status);
        }

        if (service.getStatus() == ServiceStatus.PAID) {
            throw new IllegalArgumentException("This service cant be "+ status + " because it is already PAID");
        }

        if (status == ServiceStatus.PAID) {
            if (service.getPrice() == null || service.getPrice() == 0) {
                throw new IllegalArgumentException("This service does not have an acceptable price");
            }
        }

        service.setStatus(status);

        update(service);
    }

}
