package com.api.vetgroup.dtos;

import com.api.vetgroup.models.enums.StaffRole;

public class StaffReducedDto {

    private Long id;
    private String full_name;
    private StaffRole role;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public StaffRole getRole() {
        return role;
    }

    public void setRole(StaffRole role) {
        this.role = role;
    }
}