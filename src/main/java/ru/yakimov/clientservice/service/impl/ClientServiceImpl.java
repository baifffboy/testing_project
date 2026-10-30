package ru.yakimov.clientservice.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yakimov.clientservice.dto.ClientCreateRequest;
import ru.yakimov.clientservice.dto.ClientResponse;
import ru.yakimov.clientservice.dto.ClientUpdateRequest;
import ru.yakimov.clientservice.entity.Client;
import ru.yakimov.clientservice.entity.ClientStatus;
import ru.yakimov.clientservice.exception.ClientNotFoundException;
import ru.yakimov.clientservice.mapper.ClientMapper;
import ru.yakimov.clientservice.repository.ClientRepository;
import ru.yakimov.clientservice.service.ClientService;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ClientServiceImpl implements ClientService {

    ClientRepository repository;
    ClientMapper mapper;

    @Transactional
    public ClientResponse create(ClientCreateRequest request) {
        Client client = mapper.toEntity(request);
        client.setStatus(ClientStatus.ACTIVE);
        return mapper.toResponse(repository.save(client));
    }

    public List<ClientResponse> findAll() {
        return mapper.toResponseList(repository.findAll());
    }

    public ClientResponse findById(Long id) {
        return mapper.toResponse(getOrThrow(id));
    }

    @Transactional
    public ClientResponse update(Long id, ClientUpdateRequest request) {
        Client client = getOrThrow(id);
        mapper.updateEntity(request, client);
        return mapper.toResponse(repository.saveAndFlush(client));
    }

    @Transactional
    public void delete(Long id) {
        repository.delete(getOrThrow(id));
    }

    private Client getOrThrow(Long id) {
        return repository.findById(id).orElseThrow(() -> new ClientNotFoundException(id));
    }
}