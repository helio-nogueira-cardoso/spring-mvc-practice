package br.com.helio.springmvc.dto;

import br.com.helio.springmvc.model.Client;

import java.util.UUID;

public record ClientDetails(
        UUID id,
        String customerName,
        Integer version
) {
    public ClientDetails(Client client) {
        this(
                client.getId(),
                client.getCustomerName(),
                client.getVersion()
        );
    }
}
