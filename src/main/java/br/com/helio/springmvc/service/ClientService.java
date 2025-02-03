package br.com.helio.springmvc.service;

import br.com.helio.springmvc.dto.ClientDetails;
import br.com.helio.springmvc.model.Client;

import java.util.List;
import java.util.UUID;

public interface ClientService {
    List<ClientDetails> listClients();

    ClientDetails getClientById(UUID id);
}
