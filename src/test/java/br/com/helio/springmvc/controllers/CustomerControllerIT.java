package br.com.helio.springmvc.controllers;

import br.com.helio.springmvc.dto.customer.CustomerCreationRequestDTO;
import br.com.helio.springmvc.dto.customer.CustomerDetailsDTO;
import br.com.helio.springmvc.dto.customer.CustomerUpdateRequestDTO;
import br.com.helio.springmvc.entities.Customer;
import br.com.helio.springmvc.exceptions.NotFoundException;
import br.com.helio.springmvc.repositories.CustomerRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
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

    private UUID getNotPresentUUID() {
            UUID notPresentId;
            do {
                notPresentId = UUID.randomUUID();
            } while (customerRepository.findById(notPresentId).isPresent());

            return notPresentId;
    }

    @Test
    void testBeerIdNotFound() {
        assertThrows(NotFoundException.class, () -> customerController.getCustomerById(getNotPresentUUID()));
    }

    private UUID getIdFromHeaders(HttpHeaders headers) {
        String[] locationUUID = Objects.requireNonNull(headers.getLocation())
                .getPath()
                .split("/");

        return UUID.fromString(locationUUID[4]);
    }

    @Test
    @Transactional
    @Rollback
    void testSaveNewCustomer(){
        CustomerCreationRequestDTO dto = CustomerCreationRequestDTO.builder()
                .name("New Customer")
                .build();

        ResponseEntity<HttpStatus> response = customerController.saveNewCustomer(dto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getHeaders().getLocation()).isNotNull();

        Optional<Customer> optionalCustomer = customerRepository.findById(getIdFromHeaders(response.getHeaders()));
        assertThat(optionalCustomer.isPresent()).isTrue();
    }

    @Test
    void updateExistingCustomer() {
        final String UPDATED_NAME = "UPDATED";
        CustomerUpdateRequestDTO updateRequestDto = CustomerUpdateRequestDTO.builder().name(UPDATED_NAME).build();

        Customer existingCustomer = customerRepository.findAll().getFirst();
        ResponseEntity<HttpStatus> response = customerController.updateById(existingCustomer.getId(), updateRequestDto);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        Optional<Customer> optionalCustomer = customerRepository.findById(existingCustomer.getId());
        assertThat(optionalCustomer.isPresent()).isTrue();

        Customer customer = optionalCustomer.orElse(null);
        assertThat(customer.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    @Rollback
    void updateNotExistingCustomer() {
        final String UPDATED_NAME = "UPDATED";
        final UUID ABSENT_ID = getNotPresentUUID();
        CustomerUpdateRequestDTO updateRequestDto = CustomerUpdateRequestDTO.builder().name(UPDATED_NAME).build();

        assertThat(customerRepository.existsById(ABSENT_ID)).isFalse();

        ResponseEntity<HttpStatus> response = customerController.updateById(ABSENT_ID, updateRequestDto);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getHeaders().getLocation()).isNotNull();

        assertThat(customerRepository.existsById(getIdFromHeaders(response.getHeaders()))).isTrue();
    }

    @Test
    @Transactional
    @Rollback
    void testDelete() {
        Customer existingCustomer = customerRepository.findAll().getFirst();
        final UUID ID_TO_BE_DELETED = existingCustomer.getId();

        ResponseEntity<HttpStatus> response = customerController.deleteById(ID_TO_BE_DELETED);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(customerRepository.findById(ID_TO_BE_DELETED).isEmpty()).isTrue();
    }

    @Test
    void testDeleteNotFound() {
        final UUID ABSENT_ID = getNotPresentUUID();
        assertThrows(NotFoundException.class, () -> customerController.deleteById(ABSENT_ID));
    }
}