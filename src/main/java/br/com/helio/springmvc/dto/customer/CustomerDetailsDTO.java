package br.com.helio.springmvc.dto.customer;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record CustomerDetailsDTO(
        UUID id,
        String name,
        Integer version,
        LocalDateTime createdDate,
        LocalDateTime lastModifiedDate
) {
}
