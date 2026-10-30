package ru.yakimov.clientservice.dto;

import ru.yakimov.clientservice.entity.ClientStatus;

import java.time.OffsetDateTime;

public record ClientResponse(
        String fullName,
        String gender,
        ClientStatus status,
        OffsetDateTime createDttm,
        OffsetDateTime modifyDttm
) {
}
