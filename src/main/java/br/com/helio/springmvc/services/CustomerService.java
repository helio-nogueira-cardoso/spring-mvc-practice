package br.com.helio.springmvc.services;

import br.com.helio.springmvc.dto.customer.CustomerCreationRequestDTO;
import br.com.helio.springmvc.dto.customer.CustomerDetailsDTO;
import br.com.helio.springmvc.dto.customer.CustomerUpdateRequestDTO;
import org.springframework.data.domain.Page;

import java.util.Optional;
import java.util.UUID;

public interface CustomerService {
    Page<CustomerDetailsDTO> listCustomers(String name, Integer pageNumber, Integer pageSize);

    Optional<CustomerDetailsDTO> getCustomerDetailsById(UUID id);

    CustomerDetailsDTO saveNewCustomer(CustomerCreationRequestDTO request);

    CustomerDetailsDTO updateCustomerById(UUID customerId, CustomerUpdateRequestDTO request);

    boolean deleteCustomerById(UUID customerId);

    void patchCustomerById(UUID customerId, CustomerUpdateRequestDTO request);
}
