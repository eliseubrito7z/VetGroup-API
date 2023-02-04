package com.api.vetgroup.services.customMappers;

import com.api.vetgroup.dtos.create.RoleHistoricCreateDto;
import com.api.vetgroup.models.Role;
import com.api.vetgroup.models.RoleHistoric;
import com.api.vetgroup.models.StaffUser;
import com.api.vetgroup.repositories.RoleRepository;
import com.api.vetgroup.services.StaffUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RoleHistoricMapper {

    @Autowired
    private StaffUserService staffService;

    @Autowired
    private RoleRepository roleRepository;

    public RoleHistoric convertStaffToFirstRole(StaffUser staff) {
        try {
            RoleHistoric roleHistoric = new RoleHistoric();
            StaffUser promoter = staffService.findById(1L);

            roleHistoric.setBase_salary(staff.getBase_salary());
            roleHistoric.setStarted_in(staff.getCreated_at());
            roleHistoric.setWeekly_work_load(staff.getWeekly_work_load());
            roleHistoric.setRole(staff.getRole());
            roleHistoric.setPromoter(promoter);
            roleHistoric.setStaff(staff);

            return roleHistoric;
        } catch (Exception e) {
            throw new RuntimeException("Error during conversion to RoleHistoric");
        }
    }

    public RoleHistoric convertToRoleHistoric(RoleHistoricCreateDto new_role, Long staff_id) {
        try {
            RoleHistoric roleHistoric = new RoleHistoric();
            BeanUtils.copyProperties(new_role, roleHistoric);

            StaffUser promoter = staffService.findById(new_role.getPromoted_by());
            StaffUser staff = staffService.findById(staff_id);

            Role role = roleRepository.findByDescription(new_role.getRole());

            roleHistoric.setRole(role.getId());
            roleHistoric.setPromoter(promoter);
            roleHistoric.setStaff(staff);

            return roleHistoric;
        } catch (Exception e) {
            throw new RuntimeException("Error during conversion to RoleHistoric");
        }
    }
}
