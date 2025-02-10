package br.com.helio.springmvc.services;

import br.com.helio.springmvc.dto.customer.CustomerCreationRequestDTO;
import br.com.helio.springmvc.dto.customer.CustomerDetailsDTO;
import br.com.helio.springmvc.dto.customer.CustomerUpdateRequestDTO;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerService {
    List<CustomerDetailsDTO> listCustomers(String name);

    Optional<CustomerDetailsDTO> getCustomerDetailsById(UUID id);

    CustomerDetailsDTO saveNewCustomer(CustomerCreationRequestDTO request);

    CustomerDetailsDTO updateCustomerById(UUID customerId, CustomerUpdateRequestDTO request);

    boolean deleteCustomerById(UUID customerId);

    void patchCustomerById(UUID customerId, CustomerUpdateRequestDTO request);
}
