package br.com.helio.springmvc.service;

import br.com.helio.springmvc.dto.customer.CustomerCreationRequest;
import br.com.helio.springmvc.dto.customer.CustomerDetails;
import br.com.helio.springmvc.dto.customer.CustomerUpdateRequest;
import br.com.helio.springmvc.model.Customer;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
    public List<CustomerDetails> listCustomers() {
        return customerMap.values().stream().map(CustomerDetails::new).toList();
    }

    @Override
    public CustomerDetails getCustomerDetaisById(UUID id) {
        return new CustomerDetails(customerMap.get(id));
    }

    @Override
    public CustomerDetails saveNewCustomer(CustomerCreationRequest request) {
        Customer newCustomer = Customer.builder()
                .id(UUID.randomUUID())
                .version(1)
                .name(request.name())
                .createdDate(LocalDateTime.now())
                .lastModifiedDate(LocalDateTime.now())
                .build();

        customerMap.put(newCustomer.getId(), newCustomer);

        return new CustomerDetails(newCustomer);
    }

    @Override
    public CustomerDetails updateCustomerById(UUID customerId, CustomerUpdateRequest request) {
        Customer existingCustomer = customerMap.get(customerId);

        if (existingCustomer == null) {
            return this.saveNewCustomer(new CustomerCreationRequest(request.name()));
        }

        existingCustomer.setName(request.name());
        existingCustomer.setLastModifiedDate(LocalDateTime.now());

        customerMap.put(existingCustomer.getId(), existingCustomer);

        return new CustomerDetails(existingCustomer);
    }
}
