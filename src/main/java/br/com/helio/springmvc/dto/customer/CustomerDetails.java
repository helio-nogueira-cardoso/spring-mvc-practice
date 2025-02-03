package br.com.helio.springmvc.dto.customer;

import br.com.helio.springmvc.model.Customer;

import java.time.LocalDateTime;
import java.util.UUID;

public record CustomerDetails(
        UUID id,
        String name,
        Integer version,
        LocalDateTime createdDate,
        LocalDateTime lastModifiedDate
) {
    public CustomerDetails(Customer customer) {
        this(
                customer.getId(),
                customer.getName(),
                customer.getVersion(),
                customer.getCreatedDate(),
                customer.getLastModifiedDate()
        );
    }
}
