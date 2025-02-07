package br.com.helio.springmvc.dto.customer;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record CustomerDetailsDTO(
        UUID id,
        String name,
        String email,
        Integer version,
        LocalDateTime createdDate,
        LocalDateTime lastModifiedDate
) {
    /**
     * Constructor only for previous Map Implementation of Service.
     * Later using Mappers to covert between dtos and entities.
     */
    public CustomerDetailsDTO(br.com.helio.springmvc.models.Customer customer) {
        this(
                customer.getId(),
                customer.getName(),
                customer.getEmail(),
                customer.getVersion(),
                customer.getCreatedDate(),
                customer.getLastModifiedDate()
        );
    }
}
