package br.com.helio.springmvc.services;

import br.com.helio.springmvc.dto.customer.CustomerCreationRequestDTO;
import br.com.helio.springmvc.dto.customer.CustomerDetailsDTO;
import br.com.helio.springmvc.dto.customer.CustomerUpdateRequestDTO;
import br.com.helio.springmvc.models.Customer;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class CustomerServiceMapImpl implements CustomerService {
    Map<UUID, Customer> customerMap;

    public CustomerServiceMapImpl() {
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
    public Optional<CustomerDetailsDTO> getCustomerDetailsById(UUID id) {
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
    public CustomerDetailsDTO updateCustomerById(UUID customerId, CustomerUpdateRequestDTO request) {
        Customer existingCustomer = customerMap.get(customerId);

        if (existingCustomer == null) {
            return this.saveNewCustomer(new CustomerCreationRequestDTO(request.name()));
        }

        existingCustomer.setName(request.name());
        existingCustomer.setLastModifiedDate(LocalDateTime.now());

        customerMap.put(existingCustomer.getId(), existingCustomer);

        return new CustomerDetailsDTO(existingCustomer);
    }

    @Override
    public boolean deleteCustomerById(UUID customerId) {
        if (customerMap.containsKey(customerId)) {
            customerMap.remove(customerId);
            return true;
        }

        return false;
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