package br.com.helio.springmvc.controller;

import br.com.helio.springmvc.dto.customer.CustomerCreationRequest;
import br.com.helio.springmvc.dto.customer.CustomerDetails;
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

    @PostMapping
    public ResponseEntity<HttpStatus> handlePost(@RequestBody CustomerCreationRequest request) {
        log.debug("Creating new client");
        CustomerDetails customerDetails = customerService.saveNewClient(request);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Location","/api/v1/customers/" + customerDetails.id().toString());

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
