package ru.yakimov.clientservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ClientCreateRequest(
        @NotBlank(message = "fullName не должно быть пустым")
        String fullName,

        @Pattern(regexp = "^[MFmf]$", message = "gender должен быть 'M' или 'F'")
        String gender
) {
}
