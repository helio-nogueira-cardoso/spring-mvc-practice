package br.com.helio.springmvc.repositories;

import br.com.helio.springmvc.entities.Customer;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    List<Customer> findAllByNameIsLikeIgnoreCase(@NotBlank @Size(max = 50) String name);
}
