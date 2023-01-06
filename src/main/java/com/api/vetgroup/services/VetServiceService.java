package com.api.vetgroup.services;

import com.api.vetgroup.models.Report;
import com.api.vetgroup.models.VetService;
import com.api.vetgroup.models.enums.ReportTypes;
import com.api.vetgroup.models.enums.ServiceStatus;
import com.api.vetgroup.repositories.ReportRepository;
import com.api.vetgroup.repositories.ServiceRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VetServiceService {

    @Autowired
    private ServiceRepository repository;

    public List<VetService> findAll() {return repository.findAll();}

    public VetService findById(Long id) {
        Optional<VetService> obj = repository.findById(id);
        return obj.get();
    }

    @Transactional
    public VetService insert(VetService newService) {
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

    @Transactional
    public void changeStatus(Long id, ServiceStatus status) {
        Optional<VetService> service = repository.findById(id);
        switch (status.getCode()) {
            case 1:
                service.get().setStatus(ServiceStatus.SCHEDULED);
                update(service.get());
                break;
            case 2:
                service.get().setStatus(ServiceStatus.WAITING_PAYMENT);
                update(service.get());
                break;
            case 3:
                service.get().setStatus(ServiceStatus.PAID);
                update(service.get());
                break;
            default:
                throw new IllegalArgumentException("This service status not exists");

        }
        update(service.get());
    }
}
