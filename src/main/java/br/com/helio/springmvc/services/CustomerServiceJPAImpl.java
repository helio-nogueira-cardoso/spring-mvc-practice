package br.com.helio.springmvc.services;

import br.com.helio.springmvc.dto.customer.CustomerCreationRequestDTO;
import br.com.helio.springmvc.dto.customer.CustomerDetailsDTO;
import br.com.helio.springmvc.dto.customer.CustomerUpdateRequestDTO;
import br.com.helio.springmvc.entities.Customer;
import br.com.helio.springmvc.mappers.CustomerMapper;
import br.com.helio.springmvc.repositories.CustomerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Primary
@RequiredArgsConstructor
public class CustomerServiceJPAImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Override
    public List<CustomerDetailsDTO> listCustomers() {
        return customerRepository
                .findAll()
                .stream()
                .map(customerMapper::customerToCustomerDetailsDto)
                .toList();
    }

    @Override
    public Optional<CustomerDetailsDTO> getCustomerDetaisById(UUID id) {
        return customerRepository.findById(id).map(customerMapper::customerToCustomerDetailsDto);
    }

    @Override
    public CustomerDetailsDTO saveNewCustomer(CustomerCreationRequestDTO request) {
        Customer newCustomer = Customer.builder()
                .name(request.name())
                .createdDate(LocalDateTime.now())
                .lastModifiedDate(LocalDateTime.now())
                .build();

        Customer savedCustomer = customerRepository.save(newCustomer);

        return customerMapper.customerToCustomerDetailsDto(savedCustomer);
    }

    @Override
    @Transactional
    public void updateCustomerById(UUID customerId, CustomerUpdateRequestDTO request) {
        Optional<Customer> optionalCustomer = customerRepository.findById(customerId);

        if (optionalCustomer.isPresent()) {
            Customer existingCustomer = optionalCustomer.get();
            existingCustomer.setName(request.name());
            existingCustomer.setLastModifiedDate(LocalDateTime.now());
        }
        else {
            saveNewCustomer(
                customerMapper.customerUpdateRequestDtoToCustomerCreationRequestDto(
                    request
                )
            );
        }
    }

    @Override
    public void deleteCustomerById(UUID customerId) {
        customerRepository.deleteById(customerId);
    }

    @Override
    @Transactional
    public void patchCustomerById(UUID customerId, CustomerUpdateRequestDTO request) {
        Optional<Customer> optionalCustomer = customerRepository.findById(customerId);

        if (optionalCustomer.isPresent() && request.name() != null) {
            Customer existingCustomer = optionalCustomer.get();
            existingCustomer.setName(request.name());
            existingCustomer.setLastModifiedDate(LocalDateTime.now());
        }
    }
}
