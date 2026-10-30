package ru.yakimov.clientservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yakimov.clientservice.entity.Client;

public interface ClientRepository extends JpaRepository<Client, Long> {
}
