package br.com.helio.springmvc.repositories;

import br.com.helio.springmvc.bootstrap.BootstrapData;
import br.com.helio.springmvc.entities.Customer;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
class CustomerRepositoryTest {
    @Autowired
    CustomerRepository customerRepository;

    BootstrapData bootstrapData;

    @BeforeEach
    void setUp() {
        bootstrapData = new BootstrapData(customerRepository);
    }

    @Test
    void testBootstrappedData() {
        bootstrapData.run();
        assertThat(customerRepository.count()).isEqualTo(5);
    }

    @Test
    @Transactional
    @Rollback
    void saveCustomer() {
        Customer saveCustomer = customerRepository.save(
            Customer.builder()
                .name("Helio")
                .createdDate(LocalDateTime.now())
                .lastModifiedDate(LocalDateTime.now())
                .build()
        );

        customerRepository.flush();

        assertThat(saveCustomer).isNotNull();
        assertThat(saveCustomer.getId()).isNotNull();
    }

    @Test
    @Transactional
    @Rollback
    void tryToSaveInconsistentCustomer() {
        assertThrows(ConstraintViolationException.class, () -> {
            customerRepository.save(
                    Customer.builder()
                            .name("")
                            .build()
            );

            customerRepository.flush();
        });
    }
}