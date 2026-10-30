package ru.yakimov.test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yakimov.clientservice.dto.ClientCreateRequest;
import ru.yakimov.clientservice.dto.ClientResponse;
import ru.yakimov.clientservice.dto.ClientUpdateRequest;
import ru.yakimov.clientservice.entity.Client;
import ru.yakimov.clientservice.entity.ClientStatus;
import ru.yakimov.clientservice.exception.ClientNotFoundException;
import ru.yakimov.clientservice.mapper.ClientMapper;
import ru.yakimov.clientservice.repository.ClientRepository;
import ru.yakimov.clientservice.service.impl.ClientServiceImpl;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientServiceImplTest {

    @Mock
    private ClientRepository repository;

    @Mock
    private ClientMapper mapper;

    @InjectMocks
    private ClientServiceImpl service;

    private ClientResponse response(String name, ClientStatus status) {
        OffsetDateTime now = OffsetDateTime.now();
        return new ClientResponse(name, "M", status, now, now);
    }

    @Test
    void create_savesClientAndReturnsResponse() {
        ClientCreateRequest request = new ClientCreateRequest("Ivanov Ivan", "M");

        Client client = new Client();
        client.setFullName("Ivanov Ivan");
        client.setGender("M");
        client.setStatus(ClientStatus.ACTIVE);

        ClientResponse expected = response("Ivanov Ivan", ClientStatus.ACTIVE);

        when(mapper.toEntity(any(ClientCreateRequest.class))).thenReturn(client);
        when(repository.save(any(Client.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(mapper.toResponse(client)).thenReturn(expected);

        ClientResponse response = service.create(request);

        assertEquals("Ivanov Ivan", response.fullName());
        assertEquals("M", response.gender());
        assertEquals(ClientStatus.ACTIVE, response.status());

        verify(mapper).toEntity(request);
        verify(repository).save(client);
        verify(mapper).toResponse(client);
    }

    @Test
    void findAll_returnsMappedList() {
        List<Client> clients = List.of(new Client(), new Client());
        List<ClientResponse> expected = List.of(
                response("Ivanov Ivan", ClientStatus.ACTIVE),
                response("Petrova Anna", ClientStatus.INACTIVE));

        when(repository.findAll()).thenReturn(clients);
        when(mapper.toResponseList(clients)).thenReturn(expected);

        assertThat(service.findAll()).isEqualTo(expected);
    }

    @Test
    void findById_found_returnsResponse() {
        Client client = new Client();
        ClientResponse expected = response("Ivanov Ivan", ClientStatus.ACTIVE);

        when(repository.findById(1L)).thenReturn(Optional.of(client));
        when(mapper.toResponse(client)).thenReturn(expected);

        assertThat(service.findById(1L)).isEqualTo(expected);
    }

    @Test
    void findById_notFound_throwsException() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99L))
                .isInstanceOf(ClientNotFoundException.class);
    }

    @Test
    void update_found_updatesAndSaves() {
        ClientUpdateRequest request = new ClientUpdateRequest("Ivanov Petr", null, ClientStatus.INACTIVE);
        Client client = new Client();
        ClientResponse expected = response("Ivanov Petr", ClientStatus.INACTIVE);

        when(repository.findById(1L)).thenReturn(Optional.of(client));
        when(repository.saveAndFlush(client)).thenReturn(client);
        when(mapper.toResponse(client)).thenReturn(expected);

        ClientResponse result = service.update(1L, request);

        assertThat(result).isEqualTo(expected);
        verify(mapper).updateEntity(request, client);
        verify(repository).saveAndFlush(client);
    }

    @Test
    void update_notFound_throwsAndDoesNotSave() {
        ClientUpdateRequest request = new ClientUpdateRequest("Ivanov Petr", null, null);
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(99L, request))
                .isInstanceOf(ClientNotFoundException.class);

        verify(repository, never()).saveAndFlush(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void delete_found_deletesClient() {
        Client client = new Client();
        when(repository.findById(1L)).thenReturn(Optional.of(client));

        service.delete(1L);

        verify(repository).delete(client);
    }

    @Test
    void delete_notFound_throwsAndDoesNotDelete() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(99L))
                .isInstanceOf(ClientNotFoundException.class);

        verify(repository, never()).delete(org.mockito.ArgumentMatchers.any());
    }
}