package br.com.helio.springmvc.service;

import br.com.helio.springmvc.dto.CustomerCreationRequest;
import br.com.helio.springmvc.dto.CustomerDetails;
import br.com.helio.springmvc.model.Customer;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class CustomerServiceImpl implements CustomerService {
    Map<UUID, Customer> clientMap;

    public CustomerServiceImpl() {
        clientMap = new HashMap<>();

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

        clientMap.put(customer1.getId(), customer1);
        clientMap.put(customer2.getId(), customer2);
        clientMap.put(customer3.getId(), customer3);
    }

    @Override
    public List<CustomerDetails> listCustomers() {
        return clientMap.values().stream().map(CustomerDetails::new).toList();
    }

    @Override
    public CustomerDetails getCustomerDetaisById(UUID id) {
        return new CustomerDetails(clientMap.get(id));
    }

    @Override
    public CustomerDetails saveNewClient(CustomerCreationRequest request) {
        Customer newCustomer = Customer.builder()
                .id(UUID.randomUUID())
                .version(request.version())
                .name(request.name())
                .createdDate(LocalDateTime.now())
                .lastModifiedDate(LocalDateTime.now())
                .build();

        clientMap.put(newCustomer.getId(), newCustomer);

        return new CustomerDetails(newCustomer);
    }
}
