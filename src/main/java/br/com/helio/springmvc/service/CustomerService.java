package br.com.helio.springmvc.service;

import br.com.helio.springmvc.dto.CustomerCreationRequest;
import br.com.helio.springmvc.dto.CustomerDetails;

import java.util.List;
import java.util.UUID;

public interface CustomerService {
    List<CustomerDetails> listCustomers();

    CustomerDetails getCustomerDetaisById(UUID id);

    CustomerDetails saveNewClient(CustomerCreationRequest request);
}
