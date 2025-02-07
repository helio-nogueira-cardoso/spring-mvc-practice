package br.com.helio.springmvc.dto.customer;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record CustomerCreationRequestDTO(
        @NotBlank
        String name,

        @Email
        String email
) {
}
