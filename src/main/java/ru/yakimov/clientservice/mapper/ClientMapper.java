package ru.yakimov.clientservice.mapper;

import org.mapstruct.*;
import ru.yakimov.clientservice.dto.ClientCreateRequest;
import ru.yakimov.clientservice.dto.ClientResponse;
import ru.yakimov.clientservice.dto.ClientUpdateRequest;
import ru.yakimov.clientservice.entity.Client;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ClientMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createDttm", ignore = true)
    @Mapping(target = "modifyDttm", ignore = true)
    @Mapping(target = "fullName", qualifiedByName = "trim")
    @Mapping(target = "gender", qualifiedByName = "upperCase")
    @Mapping(target = "status", constant = "ACTIVE")
    Client toEntity(ClientCreateRequest request);

    ClientResponse toResponse(Client client);

    List<ClientResponse> toResponseList(List<Client> clients);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createDttm", ignore = true)
    @Mapping(target = "modifyDttm", ignore = true)
    @Mapping(target = "fullName", qualifiedByName = "trim")
    @Mapping(target = "gender", qualifiedByName = "upperCase")
    void updateEntity(ClientUpdateRequest request, @MappingTarget Client client);

    @Named("trim")
    default String trim(String value) {
        return value == null ? null : value.trim();
    }

    @Named("upperCase")
    default String upperCase(String value) {
        return value == null ? null : value.toUpperCase();
    }
}