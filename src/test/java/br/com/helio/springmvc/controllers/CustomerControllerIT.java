package br.com.helio.springmvc.controllers;

import br.com.helio.springmvc.dto.customer.CustomerCreationRequestDTO;
import br.com.helio.springmvc.dto.customer.CustomerDetailsDTO;
import br.com.helio.springmvc.entities.Customer;
import br.com.helio.springmvc.exceptions.NotFoundException;
import br.com.helio.springmvc.repositories.CustomerRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class CustomerControllerIT {
    @Autowired
    CustomerController customerController;

    @Autowired
    CustomerRepository customerRepository;

    @Test
    void testListBeers() {
        List<CustomerDetailsDTO> dtos = customerController.listCustomers();
        assertThat(dtos.size()).isEqualTo(5);
    }

    @Test
    @Transactional
    @Rollback
    void testEmptyList() {
        customerRepository.deleteAll();
        List<CustomerDetailsDTO> dtos = customerController.listCustomers();
        assertThat(dtos.size()).isEqualTo(0);
    }

    @Test
    void testGetById() {
        Customer customer = customerRepository.findAll().getFirst();
        CustomerDetailsDTO dto = customerController.getCustomerById(customer.getId());
        assertThat(dto).isNotNull();
    }

    @Test
    void testBeerIdNotFound() {
        assertThrows(NotFoundException.class, () -> {
            UUID notPresentId;
            do {
                notPresentId = UUID.randomUUID();
            } while (customerRepository.findById(notPresentId).isPresent());

            customerController.getCustomerById(UUID.randomUUID());
        });
    }

    @Test
    @Transactional
    @Rollback
    void testSaveNewCustomer(){
        CustomerCreationRequestDTO dto = CustomerCreationRequestDTO.builder()
                .name("New Customer")
                .build();

        ResponseEntity<HttpStatus> response = customerController.handlePost(dto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getHeaders().getLocation()).isNotNull();

        String[] locationUUID = Objects.requireNonNull(
                response.getHeaders().getLocation()
        )
                .getPath()
                .split("/");

        UUID savedUUID = UUID.fromString(locationUUID[4]);

        Optional<Customer> optionalCustomer = customerRepository.findById(savedUUID);
        assertThat(optionalCustomer.isPresent()).isTrue();
    }
}