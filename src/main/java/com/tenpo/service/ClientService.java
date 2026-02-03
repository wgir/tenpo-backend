package com.tenpo.service;

import com.tenpo.api.dto.ClientRequestDTO;
import com.tenpo.api.dto.ClientResponseDTO;
import com.tenpo.model.Client;
import com.tenpo.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class ClientService {

    private final ClientRepository clientRepository;

    @Transactional
    public ClientResponseDTO createClient(ClientRequestDTO request) {
        Client client = Client.builder()
                .name(request.name())
                .rut(request.rut())
                .build();
        client = clientRepository.save(client);
        return mapToResponse(client);
    }

    @Transactional(readOnly = true)
    public List<ClientResponseDTO> getAllClients() {
        return clientRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ClientResponseDTO getClientById(Integer id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client not found"));
        return mapToResponse(client);
    }

    @Transactional
    public ClientResponseDTO updateClient(Integer id, ClientRequestDTO request) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client not found"));
        client.setName(request.name());
        client.setRut(request.rut());
        client = clientRepository.save(client);
        return mapToResponse(client);
    }

    @Transactional
    public void deleteClient(Integer id) {
        clientRepository.deleteById(id);
    }

    private ClientResponseDTO mapToResponse(@org.springframework.lang.NonNull Client client) {
        return ClientResponseDTO.builder()
                .id(client.getId())
                .name(client.getName())
                .rut(client.getRut())
                .build();
    }
}
