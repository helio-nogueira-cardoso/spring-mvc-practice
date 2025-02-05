package br.com.helio.springmvc.bootstrap;

import br.com.helio.springmvc.dto.customer.CustomerCreationRequestDTO;
import br.com.helio.springmvc.dto.customer.CustomerDetailsDTO;
import br.com.helio.springmvc.services.CustomerService;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class BootstrapData implements CommandLineRunner {
    private final CustomerService customerService;
    private final List<UUID> registeredCustomerIds = new ArrayList<>();

    private void addNewCustomer(CustomerCreationRequestDTO customerCreationRequest) {
        CustomerDetailsDTO savedCustomerDetails = customerService.saveNewCustomer(customerCreationRequest);
        registeredCustomerIds.add(savedCustomerDetails.id());
        log.debug(" ## Adding bootstrap customer | id = {}", savedCustomerDetails.id());
    }

    @Override
    public void run(String... args) {
        addNewCustomer(CustomerCreationRequestDTO.builder().name("Customer 1").build());
        addNewCustomer(CustomerCreationRequestDTO.builder().name("Customer 2").build());
        addNewCustomer(CustomerCreationRequestDTO.builder().name("Customer 3").build());
        addNewCustomer(CustomerCreationRequestDTO.builder().name("Customer 4").build());
        addNewCustomer(CustomerCreationRequestDTO.builder().name("Customer 5").build());
    }

    /**
     * Maybe not necessary, because we are now using an in-memory H2 database.
     * However, it was nice to practice, and might be handful for something in
     * the future.
     */
    @PreDestroy
    public void destroyAllBootstrappedData() {
        log.debug("## Destroying all bootstrapped data");
        registeredCustomerIds.forEach(customerService::deleteCustomerById);
    }
}
