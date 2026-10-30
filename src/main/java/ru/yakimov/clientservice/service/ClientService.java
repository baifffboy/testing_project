package ru.yakimov.clientservice.service;

import ru.yakimov.clientservice.dto.ClientCreateRequest;
import ru.yakimov.clientservice.dto.ClientResponse;
import ru.yakimov.clientservice.dto.ClientUpdateRequest;

import java.util.List;

public interface ClientService {

    ClientResponse create(ClientCreateRequest request);

    List<ClientResponse> findAll();

    ClientResponse findById(Long id);

    ClientResponse update(Long id, ClientUpdateRequest request);

    void delete(Long id);
}
