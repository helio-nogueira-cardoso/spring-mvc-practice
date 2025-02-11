package br.com.helio.springmvc.services;

import br.com.helio.springmvc.dto.customer.CustomerCreationRequestDTO;
import br.com.helio.springmvc.dto.customer.CustomerDetailsDTO;
import br.com.helio.springmvc.dto.customer.CustomerUpdateRequestDTO;
import br.com.helio.springmvc.entities.Customer;
import br.com.helio.springmvc.mappers.CustomerMapper;
import br.com.helio.springmvc.repositories.CustomerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@Service
@Primary
@RequiredArgsConstructor
@Slf4j
public class CustomerServiceJPAImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    public static final int DEFAULT_PAGE_SIZE = 25;
    public static final int DEFAULT_PAGE_NUMBER = 0;

    @Override
    public Page<CustomerDetailsDTO> listCustomers(String name, Integer pageNumber, Integer pageSize) {
        Page<Customer> customers;
        Pageable pageable = buildPageRequest(pageNumber, pageSize);

        if (StringUtils.hasText(name)) {
            customers = customerRepository.findAllByNameIsLikeIgnoreCase(
                    name,
                    buildPageRequest(pageNumber, pageSize)
            );
        } else {
            customers = customerRepository.findAll(pageable);
        }

        return customers.map(customerMapper::customerToCustomerDetailsDto);
    }

    public PageRequest buildPageRequest(Integer pageNumber, Integer pageSize) {
        int queryPageNumber = DEFAULT_PAGE_NUMBER;
        int queryPageSize = DEFAULT_PAGE_SIZE;

        if (pageNumber != null && pageNumber >= 1) {
            queryPageNumber = pageNumber - 1;
        }

        if (pageSize != null && pageSize > 0) {
            if (pageSize > 1000) {
                queryPageSize = 1000;
            } else {
                queryPageSize = pageSize;
            }
        }

        Sort sort = Sort.by(Sort.Order.asc("name"));

        return PageRequest.of(queryPageNumber, queryPageSize, sort);
    }

    @Override
    public Optional<CustomerDetailsDTO> getCustomerDetailsById(UUID id) {
        return customerRepository.findById(id).map(customerMapper::customerToCustomerDetailsDto);
    }

    @Override
    @Transactional
    public CustomerDetailsDTO saveNewCustomer(CustomerCreationRequestDTO request) {
        Customer newCustomer = Customer.builder()
                .name(request.name())
                .email(request.email())
                .createdDate(LocalDateTime.now())
                .lastModifiedDate(LocalDateTime.now())
                .build();

        Customer savedCustomer = customerRepository.save(newCustomer);
        return customerMapper.customerToCustomerDetailsDto(savedCustomer);
    }

    @Override
    @Transactional
    public CustomerDetailsDTO updateCustomerById(UUID customerId, CustomerUpdateRequestDTO request) {
        AtomicReference<CustomerDetailsDTO> returnCustomer = new AtomicReference<>();

        customerRepository.findById(customerId).ifPresentOrElse(foundCustomer -> {
            foundCustomer.setName(request.name());
            foundCustomer.setEmail(request.email());
            foundCustomer.setLastModifiedDate(LocalDateTime.now());
            returnCustomer.set(customerMapper.customerToCustomerDetailsDto(foundCustomer));
        }, () -> returnCustomer.set(
                saveNewCustomer(customerMapper.customerUpdateRequestDtoToCustomerCreationRequestDto(
                        request
                ))
        ));

        return returnCustomer.get();
    }

    @Override
    public boolean deleteCustomerById(UUID customerId) {
        if (customerRepository.existsById(customerId)) {
            customerRepository.deleteById(customerId);
            return true;
        }

        return false;
    }

    @Override
    @Transactional
    public void patchCustomerById(UUID customerId, CustomerUpdateRequestDTO request) {
        Optional<Customer> optionalCustomer = customerRepository.findById(customerId);

        if (optionalCustomer.isPresent()) {
            Customer existingCustomer = optionalCustomer.get();
            boolean modified = false;

            if (request.name() != null) {
                existingCustomer.setName(request.name());
                modified = true;
            }

            if (request.email() != null) {
                existingCustomer.setEmail(request.email());
                modified = true;
            }

            if (modified) {
                existingCustomer.setLastModifiedDate(LocalDateTime.now());
            }
        }
    }
}
