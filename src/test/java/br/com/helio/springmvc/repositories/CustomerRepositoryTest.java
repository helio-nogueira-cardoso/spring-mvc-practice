package br.com.helio.springmvc.repositories;

import br.com.helio.springmvc.bootstrap.BootstrapData;
import br.com.helio.springmvc.entities.Customer;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CustomerRepositoryTest {
    @Autowired
    CustomerRepository customerRepository;

    BootstrapData bootstrapData;

    @BeforeEach
    void setUp() {
        bootstrapData = new BootstrapData(customerRepository);
    }

    @Test
    @Order(1)
    void testBootstrappedData() {
        bootstrapData.run();
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