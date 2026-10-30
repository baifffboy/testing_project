package ru.yakimov.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yakimov.clientservice.controller.ClientController;
import ru.yakimov.clientservice.dto.ClientCreateRequest;
import ru.yakimov.clientservice.dto.ClientResponse;
import ru.yakimov.clientservice.dto.ClientUpdateRequest;
import ru.yakimov.clientservice.entity.ClientStatus;
import ru.yakimov.clientservice.exception.ClientNotFoundException;
import ru.yakimov.clientservice.service.ClientService;

import java.time.OffsetDateTime;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClientController.class)
class ClientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ClientService service;

    private ClientResponse response(String name, String gender, ClientStatus status) {
        OffsetDateTime now = OffsetDateTime.now();
        return new ClientResponse(name, gender, status, now, now);
    }

    // ---------- POST /api/clients ----------

    @Test
    void create_validRequest_returns201() throws Exception {
        ClientCreateRequest request = new ClientCreateRequest("Ivanov Ivan", "M");
        when(service.create(any(ClientCreateRequest.class)))
                .thenReturn(response("Ivanov Ivan", "M", ClientStatus.ACTIVE));

        mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").doesNotExist())
                .andExpect(jsonPath("$.fullName").value("Ivanov Ivan"))
                .andExpect(jsonPath("$.gender").value("M"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void create_blankFullName_returns400() throws Exception {
        ClientCreateRequest request = new ClientCreateRequest("  ", "M");

        mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_invalidGender_returns400() throws Exception {
        ClientCreateRequest request = new ClientCreateRequest("Ivanov Ivan", "X");

        mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_malformedJson_returns400() throws Exception {
        mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ not a json"))
                .andExpect(status().isBadRequest());
    }

    // ---------- GET /api/clients ----------

    @Test
    void getAll_returns200AndList() throws Exception {
        when(service.findAll()).thenReturn(List.of(
                response("Ivanov Ivan", "M", ClientStatus.ACTIVE),
                response("Petrova Anna", "F", ClientStatus.INACTIVE)));

        mockMvc.perform(get("/api/clients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].fullName").value("Ivanov Ivan"))
                .andExpect(jsonPath("$[1].status").value("INACTIVE"));
    }

    @Test
    void getAll_emptyList_returns200() throws Exception {
        when(service.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/clients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // ---------- GET /api/clients/{id} ----------

    @Test
    void getById_found_returns200() throws Exception {
        when(service.findById(1L)).thenReturn(response("Ivanov Ivan", "M", ClientStatus.ACTIVE));

        mockMvc.perform(get("/api/clients/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").doesNotExist())
                .andExpect(jsonPath("$.fullName").value("Ivanov Ivan"));
    }

    @Test
    void getById_notFound_returns404() throws Exception {
        when(service.findById(99L)).thenThrow(new ClientNotFoundException(99L));

        mockMvc.perform(get("/api/clients/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("99")));
    }

    @Test
    void getById_idIsNotNumber_returns400() throws Exception {
        mockMvc.perform(get("/api/clients/abc"))
                .andExpect(status().isBadRequest());
    }

    // ---------- PUT /api/clients/{id} ----------

    @Test
    void update_found_returns200() throws Exception {
        ClientUpdateRequest request = new ClientUpdateRequest("Ivanov Petr", null, ClientStatus.INACTIVE);
        when(service.update(eq(1L), any(ClientUpdateRequest.class)))
                .thenReturn(response("Ivanov Petr", "M", ClientStatus.INACTIVE));

        mockMvc.perform(put("/api/clients/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Ivanov Petr"))
                .andExpect(jsonPath("$.status").value("INACTIVE"));
    }

    @Test
    void update_notFound_returns404() throws Exception {
        ClientUpdateRequest request = new ClientUpdateRequest("Ivanov Petr", null, null);
        when(service.update(eq(99L), any(ClientUpdateRequest.class)))
                .thenThrow(new ClientNotFoundException(99L));

        mockMvc.perform(put("/api/clients/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void update_invalidGender_returns400() throws Exception {
        ClientUpdateRequest request = new ClientUpdateRequest(null, "X", null);

        mockMvc.perform(put("/api/clients/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update_invalidStatus_returns400() throws Exception {
        mockMvc.perform(put("/api/clients/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"BANNED\"}"))
                .andExpect(status().isBadRequest());
    }

    // ---------- DELETE /api/clients/{id} ----------

    @Test
    void delete_found_returns204() throws Exception {
        mockMvc.perform(delete("/api/clients/1"))
                .andExpect(status().isNoContent());

        verify(service).delete(1L);
    }

    @Test
    void delete_notFound_returns404() throws Exception {
        doThrow(new ClientNotFoundException(99L)).when(service).delete(99L);

        mockMvc.perform(delete("/api/clients/99"))
                .andExpect(status().isNotFound());
    }
}