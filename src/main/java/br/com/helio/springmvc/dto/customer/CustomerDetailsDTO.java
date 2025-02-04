package br.com.helio.springmvc.dto.customer;

import br.com.helio.springmvc.models.Customer;

import java.time.LocalDateTime;
import java.util.UUID;

public record CustomerDetailsDTO(
        UUID id,
        String name,
        Integer version,
        LocalDateTime createdDate,
        LocalDateTime lastModifiedDate
) {
    public CustomerDetailsDTO(Customer customer) {
        this(
                customer.getId(),
                customer.getName(),
                customer.getVersion(),
                customer.getCreatedDate(),
                customer.getLastModifiedDate()
        );
    }
}
