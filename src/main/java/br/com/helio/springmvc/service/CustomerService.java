package br.com.helio.springmvc.service;

import br.com.helio.springmvc.dto.customer.CustomerCreationRequest;
import br.com.helio.springmvc.dto.customer.CustomerDetails;
import br.com.helio.springmvc.dto.customer.CustomerUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface CustomerService {
    List<CustomerDetails> listCustomers();

    CustomerDetails getCustomerDetaisById(UUID id);

    CustomerDetails saveNewCustomer(CustomerCreationRequest request);

    CustomerDetails updateCustomerById(UUID customerId, CustomerUpdateRequest request);
}
