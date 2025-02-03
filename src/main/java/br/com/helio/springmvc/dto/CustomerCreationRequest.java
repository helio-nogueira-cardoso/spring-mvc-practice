package br.com.helio.springmvc.dto;

public record CustomerCreationRequest(
        String name,
        Integer version
) {
}
