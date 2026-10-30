package ru.yakimov.clientservice.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yakimov.clientservice.dto.ClientCreateRequest;
import ru.yakimov.clientservice.dto.ClientResponse;
import ru.yakimov.clientservice.dto.ClientUpdateRequest;
import ru.yakimov.clientservice.service.ClientService;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("/api/clients")
public class ClientController {

    ClientService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClientResponse create(@Valid @RequestBody ClientCreateRequest request) {
        log.info("Сделан запрос на создание пользователя с именем {}", request.fullName());
        return service.create(request);
    }

    @GetMapping
    public List<ClientResponse> getAll() {
        log.info("Сделан запрос на получение списка всех пользователей");
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ClientResponse getById(@PathVariable Long id) {
        log.info("Сделан запрос на получение пользователя с id {}", id);
        return service.findById(id);
    }

    @PutMapping("/{id}")
    public ClientResponse update(@PathVariable Long id, @Valid @RequestBody ClientUpdateRequest request) {
        log.info("Сделан запрос на обновление пользователя с id {}: {}", id, request);
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        log.info("Сделан запрос на удаление пользователя с id {}", id);
        service.delete(id);
    }
}
