package ru.yakimov.clientservice.dto;

import java.util.List;

public record ClientActiveResponse(
        List<ClientResponse> list,
        Integer count
) {
}

