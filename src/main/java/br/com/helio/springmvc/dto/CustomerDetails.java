package br.com.helio.springmvc.dto;

import br.com.helio.springmvc.model.Customer;

import java.util.UUID;

public record CustomerDetails(
        UUID id,
        String name,
        Integer version
) {
    public CustomerDetails(Customer customer) {
        this(
                customer.getId(),
                customer.getName(),
                customer.getVersion()
        );
    }
}
