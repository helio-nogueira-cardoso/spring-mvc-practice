package br.com.helio.springmvc.controller;

import br.com.helio.springmvc.dto.ClientDetails;
import br.com.helio.springmvc.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/clients")
public class ClientController {
    private final ClientService clientService;

    @GetMapping
    public List<ClientDetails> listClients() {
        return clientService.listClients();
    }

    @GetMapping("{clientId}")
    public ClientDetails getClientById(@PathVariable("clientId") UUID clientID) {
        return clientService.getClientById(clientID);
    }
}
