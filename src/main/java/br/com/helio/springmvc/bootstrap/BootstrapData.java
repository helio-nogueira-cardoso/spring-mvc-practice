package br.com.helio.springmvc.bootstrap;

import br.com.helio.springmvc.entities.Customer;
import br.com.helio.springmvc.repositories.CustomerRepository;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class BootstrapData implements CommandLineRunner {
    private final CustomerRepository customerRepository;
    private final List<UUID> registeredCustomerIds = new ArrayList<>();

    private void addNewCustomer(Customer.CustomerBuilder newCustomerBuilder) {
        newCustomerBuilder.createdDate(LocalDateTime.now());
        newCustomerBuilder.lastModifiedDate(LocalDateTime.now());
        Customer savedCustomer = customerRepository.save(newCustomerBuilder.build());
        registeredCustomerIds.add(savedCustomer.getId());
        log.debug(" ## Adding bootstrap customer | id = {}", savedCustomer.getId());
    }

    @Override
    public void run(String... args) {
        addNewCustomer(Customer.builder().name("Customer 1"));
        addNewCustomer(Customer.builder().name("Customer 2"));
        addNewCustomer(Customer.builder().name("Customer 3"));
        addNewCustomer(Customer.builder().name("Customer 4"));
        addNewCustomer(Customer.builder().name("Customer 5"));
    }

    /**
     * Maybe not necessary, because we are now using an in-memory H2 database.
     * However, it was nice to practice, and might be handful for something in
     * the future.
     */
    @PreDestroy
    public void destroyAllBootstrappedData() {
        log.debug("## Destroying all bootstrapped data");
        customerRepository.deleteAllById(registeredCustomerIds);
    }
}
