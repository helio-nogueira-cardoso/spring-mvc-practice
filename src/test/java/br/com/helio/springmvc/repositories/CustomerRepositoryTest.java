package br.com.helio.springmvc.repositories;

import br.com.helio.springmvc.entities.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CustomerRepositoryTest {
    @Autowired
    CustomerRepository customerRepository;

    @Test
    void saveCustomer() {
        Customer saveCustomer = customerRepository.save(
            Customer.builder()
                .name("Helio")
                .createdDate(LocalDateTime.now())
                .lastModifiedDate(LocalDateTime.now())
                .build()
        );

        assertThat(saveCustomer).isNotNull();
        assertThat(saveCustomer.getId()).isNotNull();
    }
}