package br.com.helio.springmvc.service;

import br.com.helio.springmvc.dto.ClientDetails;
import br.com.helio.springmvc.model.Client;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ClientServiceImpl implements ClientService {
    Map<UUID, Client> clientMap;

    public ClientServiceImpl() {
        clientMap = new HashMap<>();

        Client client1 = Client.builder()
                .id(UUID.randomUUID())
                .version(1)
                .customerName("Alejadro Borges")
                .createdDate(LocalDateTime.now())
                .lastModifiedDate(LocalDateTime.now())
                .build();

        Client client2 = Client.builder()
                .id(UUID.randomUUID())
                .version(1)
                .customerName("Antonio Carlos Tirezias")
                .createdDate(LocalDateTime.now())
                .lastModifiedDate(LocalDateTime.now())
                .build();

        Client client3 = Client.builder()
                .id(UUID.randomUUID())
                .version(1)
                .customerName("John Grock")
                .createdDate(LocalDateTime.now())
                .lastModifiedDate(LocalDateTime.now())
                .build();

        clientMap.put(client1.getId(), client1);
        clientMap.put(client2.getId(), client2);
        clientMap.put(client3.getId(), client3);
    }

    @Override
    public List<ClientDetails> listClients() {
        return clientMap.values().stream().map(ClientDetails::new).toList();
    }

    @Override
    public ClientDetails getClientById(UUID id) {
        return new ClientDetails(clientMap.get(id));
    }
}
