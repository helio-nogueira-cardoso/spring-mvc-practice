package br.com.helio.springmvc.repositories;

import br.com.helio.springmvc.bootstrap.BootstrapData;
import br.com.helio.springmvc.entities.Customer;
import br.com.helio.springmvc.services.BeerCSVService;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import net.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;

import java.io.FileNotFoundException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@ActiveProfiles("localmysql")
class CustomerRepositoryTest {
    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    BeerRepository beerRepository;

    @Autowired
    BeerCSVService beerCSVService;

    BootstrapData bootstrapData;

    @BeforeEach
    void setUp() {
        bootstrapData = new BootstrapData(customerRepository, beerRepository, beerCSVService);
    }

    @Test
    void testBootstrappedData() throws FileNotFoundException {
        long count = customerRepository.count();
        bootstrapData.run();
        assertThat(customerRepository.count()).isEqualTo(count + 5);
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

    @Test
    @Transactional
    @Rollback
    void tryToSaveTooLongNameCustomer() {
        assertThrows(ConstraintViolationException.class, () -> {
            customerRepository.save(
                    Customer.builder()
                            .name(RandomString.make(51))
                            .createdDate(LocalDateTime.now())
                            .lastModifiedDate(LocalDateTime.now())
                            .build()
            );

            customerRepository.flush();
        });
    }
}