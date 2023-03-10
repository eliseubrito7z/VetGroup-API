package com.api.vetgroup.controllers;

import com.api.vetgroup.dtos.create.PatientCreateDto;
import com.api.vetgroup.dtos.response.PatientResponseDto;
import com.api.vetgroup.models.Patient;
import com.api.vetgroup.services.PatientService;
import com.api.vetgroup.services.customMappers.PatientMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping(value = "/api/patients/v1")
public class PatientController {

    @Autowired
    private PatientService service;

    @Autowired
    private PatientMapper mapper;

    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> createNewPatient(
            @RequestBody @Valid PatientCreateDto patientCreateDto,
            HttpServletRequest req
        )
    {
        try {
            String token = req.getHeader("Authorization");
            patientCreateDto.setCreatedAt(LocalDateTime.now(ZoneId.of("America/Sao_Paulo")));
            service.insert(mapper.convertDtoToPatient(patientCreateDto, token));

            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e);
        }
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity findAll(
            @RequestParam(value = "sort_by", required = false, defaultValue = "id") String sort_by,
            @RequestParam(value = "direction", required = false, defaultValue = "ASC") String direction
            )
    {
        try {
            List<Patient> list = service.findAll(sort_by, direction);
            List<PatientResponseDto> listDto = mapper.convertListToDto(list);
            return ResponseEntity.ok().body(listDto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e);
        }
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity findByID(@PathVariable Long id) {
        try {
            Patient patient = service.findById(id);
            PatientResponseDto patientDto = mapper.convertPatientToDto(patient);

            return ResponseEntity.status(HttpStatus.OK).body(patientDto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e);
        }
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity deleteById(@PathVariable Long id) {
        try {
            service.delete(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e);
        }
    }
}
