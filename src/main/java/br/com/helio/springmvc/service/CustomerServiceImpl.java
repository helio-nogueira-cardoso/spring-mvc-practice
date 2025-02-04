package br.com.helio.springmvc.service;

import br.com.helio.springmvc.dto.customer.CustomerCreationRequestDTO;
import br.com.helio.springmvc.dto.customer.CustomerDetailsDTO;
import br.com.helio.springmvc.dto.customer.CustomerUpdateRequestDTO;
import br.com.helio.springmvc.model.Customer;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class CustomerServiceImpl implements CustomerService {
    Map<UUID, Customer> customerMap;

    public CustomerServiceImpl() {
        customerMap = new HashMap<>();

        Customer customer1 = Customer.builder()
                .id(UUID.randomUUID())
                .version(1)
                .name("Alejadro Borges")
                .createdDate(LocalDateTime.now())
                .lastModifiedDate(LocalDateTime.now())
                .build();

        Customer customer2 = Customer.builder()
                .id(UUID.randomUUID())
                .version(1)
                .name("Antonio Carlos Tirezias")
                .createdDate(LocalDateTime.now())
                .lastModifiedDate(LocalDateTime.now())
                .build();

        Customer customer3 = Customer.builder()
                .id(UUID.randomUUID())
                .version(1)
                .name("John Grock")
                .createdDate(LocalDateTime.now())
                .lastModifiedDate(LocalDateTime.now())
                .build();

        customerMap.put(customer1.getId(), customer1);
        customerMap.put(customer2.getId(), customer2);
        customerMap.put(customer3.getId(), customer3);
    }

    @Override
    public List<CustomerDetailsDTO> listCustomers() {
        return customerMap.values().stream().map(CustomerDetailsDTO::new).toList();
    }

    @Override
    public Optional<CustomerDetailsDTO> getCustomerDetaisById(UUID id) {
        return Optional.of(new CustomerDetailsDTO(customerMap.get(id)));
    }

    @Override
    public CustomerDetailsDTO saveNewCustomer(CustomerCreationRequestDTO request) {
        Customer newCustomer = Customer.builder()
                .id(UUID.randomUUID())
                .version(1)
                .name(request.name())
                .createdDate(LocalDateTime.now())
                .lastModifiedDate(LocalDateTime.now())
                .build();

        customerMap.put(newCustomer.getId(), newCustomer);

        return new CustomerDetailsDTO(newCustomer);
    }

    @Override
    public void updateCustomerById(UUID customerId, CustomerUpdateRequestDTO request) {
        Customer existingCustomer = customerMap.get(customerId);

        if (existingCustomer == null) {
            this.saveNewCustomer(new CustomerCreationRequestDTO(request.name()));
            return;
        }

        existingCustomer.setName(request.name());
        existingCustomer.setLastModifiedDate(LocalDateTime.now());

        customerMap.put(existingCustomer.getId(), existingCustomer);
    }

    @Override
    public void deleteCustomerById(UUID customerId) {
        customerMap.remove(customerId);
    }

    @Override
    public void patchCustomerById(UUID customerId, CustomerUpdateRequestDTO request) {
        Customer existingCustomer = customerMap.get(customerId);

        if (existingCustomer != null && request.name() != null) {
            existingCustomer.setName(request.name());
            existingCustomer.setLastModifiedDate(LocalDateTime.now());
            customerMap.put(existingCustomer.getId(), existingCustomer);
        }
    }
}
