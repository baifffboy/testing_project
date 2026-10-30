package ru.yakimov.clientservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import ru.yakimov.clientservice.entity.ClientStatus;

public record ClientUpdateRequest(

        @NotNull
        String fullName,

        @Pattern(regexp = "^[MFmf]$", message = "gender должен быть 'M' или 'F'")
        String gender,

        ClientStatus status
) {
}
