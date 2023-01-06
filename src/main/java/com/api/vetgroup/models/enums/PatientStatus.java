package com.api.vetgroup.models.enums;

public enum PatientStatus {

    NOT_INITIALIZED(1),
    IN_PROGRESS(2),
    COMPLETED(3);

    private int code;

    private PatientStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static PatientStatus valueOf(int code) {
        for (PatientStatus value : PatientStatus.values()) {
            if (value.getCode() == code) {
                return value;
            }
        }
        throw new IllegalArgumentException("Invalid PatientKind code");
    }
}
