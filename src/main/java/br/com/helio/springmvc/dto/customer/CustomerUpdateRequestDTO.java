package br.com.helio.springmvc.dto.customer;

import lombok.Builder;

@Builder
public record CustomerUpdateRequestDTO(
        String name
) {
}
