package br.com.helio.springmvc.controller;

import br.com.helio.springmvc.dto.customer.CustomerCreationRequest;
import br.com.helio.springmvc.dto.customer.CustomerDetails;
import br.com.helio.springmvc.dto.customer.CustomerUpdateRequest;
import br.com.helio.springmvc.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/customers")
@Slf4j
public class CustomerController {
    private final CustomerService customerService;

    private HttpHeaders buildLocationHeader(UUID customerId) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location","/api/v1/customers/" + customerId.toString());
        return headers;
    }

    @PatchMapping("{customerId}")
    public ResponseEntity<HttpStatus> patchCustomerById(
            @PathVariable("customerId") UUID customerId,
            @RequestBody CustomerUpdateRequest request
    ) {
        customerService.patchCustomerById(customerId, request);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @DeleteMapping("{customerId}")
    public ResponseEntity<HttpStatus> deleteById(@PathVariable("customerId") UUID customerId) {
        customerService.deleteCustomerById(customerId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("{customerId}")
    public ResponseEntity<HttpStatus> updateById(
            @PathVariable("customerId") UUID customerId,
            @RequestBody CustomerUpdateRequest request
    ) {
        customerService.updateCustomerById(customerId, request);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping
    public ResponseEntity<HttpStatus> handlePost(@RequestBody CustomerCreationRequest request) {
        log.debug("Creating new client");
        CustomerDetails customerDetails = customerService.saveNewCustomer(request);
        HttpHeaders headers = buildLocationHeader(customerDetails.id());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @GetMapping
    public List<CustomerDetails> listCustomers() {
        return customerService.listCustomers();
    }

    @GetMapping("{clientId}")
    public CustomerDetails getCustomerById(@PathVariable("clientId") UUID clientID) {
        return customerService.getCustomerDetaisById(clientID);
    }
}
