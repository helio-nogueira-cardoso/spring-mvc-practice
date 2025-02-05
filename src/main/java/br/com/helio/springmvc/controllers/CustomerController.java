package br.com.helio.springmvc.controllers;

import br.com.helio.springmvc.dto.customer.CustomerCreationRequestDTO;
import br.com.helio.springmvc.dto.customer.CustomerDetailsDTO;
import br.com.helio.springmvc.dto.customer.CustomerUpdateRequestDTO;
import br.com.helio.springmvc.exceptions.NotFoundException;
import br.com.helio.springmvc.services.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CustomerController {
    public static final String CUSTOMER_PATH = "/api/v1/customers";
    public static final String CUSTOMER_ID_PATH_VARIABLE_NAME = "customerId";
    public static final String CUSTOMER_PATH_ID = CUSTOMER_PATH + "/{" + CUSTOMER_ID_PATH_VARIABLE_NAME + "}";

    private final CustomerService customerService;

    private HttpHeaders buildLocationHeader(UUID customerId) {
        HttpHeaders headers = new HttpHeaders();
        String location = UriComponentsBuilder
            .fromPath(CUSTOMER_PATH_ID)
            .buildAndExpand(customerId.toString())
            .toUriString();

        headers.add("Location", location);
        return headers;
    }

    @PatchMapping(CUSTOMER_PATH_ID)
    public ResponseEntity<HttpStatus> patchCustomerById(
            @PathVariable(CUSTOMER_ID_PATH_VARIABLE_NAME) UUID customerId,
            @RequestBody CustomerUpdateRequestDTO request
    ) {
        customerService.patchCustomerById(customerId, request);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping(CUSTOMER_PATH_ID)
    public ResponseEntity<HttpStatus> deleteById(@PathVariable(CUSTOMER_ID_PATH_VARIABLE_NAME) UUID customerId) {
        customerService.deleteCustomerById(customerId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping(CUSTOMER_PATH_ID)
    public ResponseEntity<HttpStatus> updateById(
            @PathVariable(CUSTOMER_ID_PATH_VARIABLE_NAME) UUID customerId,
            @RequestBody CustomerUpdateRequestDTO request
    ) {
        customerService.updateCustomerById(customerId, request);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping(CUSTOMER_PATH)
    public ResponseEntity<HttpStatus> handlePost(@RequestBody CustomerCreationRequestDTO request) {
        log.debug("Creating new client");
        CustomerDetailsDTO customerDetailsDTO = customerService.saveNewCustomer(request);
        HttpHeaders headers = buildLocationHeader(customerDetailsDTO.id());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @GetMapping(CUSTOMER_PATH)
    public List<CustomerDetailsDTO> listCustomers() {
        return customerService.listCustomers();
    }

    @GetMapping(CUSTOMER_PATH_ID)
    public CustomerDetailsDTO getCustomerById(@PathVariable(CUSTOMER_ID_PATH_VARIABLE_NAME) UUID customerId) {
        return customerService.getCustomerDetaisById(customerId).orElseThrow(NotFoundException::new);
    }
}
