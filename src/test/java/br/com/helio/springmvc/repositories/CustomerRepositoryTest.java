package br.com.helio.springmvc.repositories;

import br.com.helio.springmvc.entities.Customer;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CustomerRepositoryTest {
    @Autowired
    CustomerRepository customerRepository;

    @Test
    @Order(1)
    void testBootstrappedData() {
        assertThat(customerRepository.count()).isEqualTo(5);
    }

    @Test
    @Order(2)
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