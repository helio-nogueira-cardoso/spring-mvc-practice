package br.com.helio.springmvc.controller;

import br.com.helio.springmvc.dto.CustomerCreationRequest;
import br.com.helio.springmvc.dto.CustomerDetails;
import br.com.helio.springmvc.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/clients")
@Slf4j
public class CustomerController {
    private final CustomerService customerService;

    @PostMapping
    public ResponseEntity<HttpStatus> handlePost(@RequestBody CustomerCreationRequest request) {
        log.debug("Creating new client");
        customerService.saveNewClient(request);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping
    public List<CustomerDetails> listClients() {
        return customerService.listClients();
    }

    @GetMapping("{clientId}")
    public CustomerDetails getClientById(@PathVariable("clientId") UUID clientID) {
        return customerService.getClientById(clientID);
    }
}
