package ru.yakimov.clientservice.exception;

public class ClientNotFoundException extends RuntimeException {
    public ClientNotFoundException(Long id) {
        super("Клиент с id=" + id + " не найден");
    }
}
