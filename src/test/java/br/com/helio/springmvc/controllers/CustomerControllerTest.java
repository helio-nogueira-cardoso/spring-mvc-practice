package br.com.helio.springmvc.controllers;

import br.com.helio.springmvc.dto.customer.CustomerCreationRequestDTO;
import br.com.helio.springmvc.dto.customer.CustomerDetailsDTO;
import br.com.helio.springmvc.dto.customer.CustomerUpdateRequestDTO;
import br.com.helio.springmvc.services.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.core.Is.is;

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    CustomerService customerService;

    @Captor
    ArgumentCaptor<CustomerUpdateRequestDTO> customerUpdateRequestArgumentCaptor;

    private final List<CustomerDetailsDTO> customersList = new ArrayList<>(List.of(
        new CustomerDetailsDTO(
                UUID.randomUUID(),
                "Test Customer 1",
                1,
                LocalDateTime.now(),
                LocalDateTime.now()
        ),
        new CustomerDetailsDTO(
                UUID.randomUUID(),
                "Test Customer 2",
                1,
                LocalDateTime.now(),
                LocalDateTime.now()
        ),
        new CustomerDetailsDTO(
                UUID.randomUUID(),
                "Test Customer 3",
                1,
                LocalDateTime.now(),
                LocalDateTime.now()
        )
    ));

    @Test
    void getCustomerById() throws Exception {
        CustomerDetailsDTO testCustomerDetailsDTO = customersList.getFirst();

        when(customerService.getCustomerDetaisById(testCustomerDetailsDTO.id()))
                .thenReturn(Optional.of(testCustomerDetailsDTO));

        mockMvc
            .perform(
                get(CustomerController.CUSTOMER_PATH_ID, testCustomerDetailsDTO.id())
                    .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id",  is(testCustomerDetailsDTO.id().toString())))
            .andExpect(jsonPath("$.name", is(testCustomerDetailsDTO.name())));
    }

    @Test
    void getCustomerByIdNotFound() throws Exception {
        when(customerService.getCustomerDetaisById(any(UUID.class)))
                .thenReturn(Optional.empty());

        mockMvc
            .perform(
                get(CustomerController.CUSTOMER_PATH_ID, UUID.randomUUID())
                    .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isNotFound());
    }

    @Test
    void listCustomers() throws Exception {
        when(customerService.listCustomers())
                .thenReturn(customersList);

        mockMvc
            .perform(
                get(CustomerController.CUSTOMER_PATH)
                    .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.length()", is(customersList.size())));
    }

    @Test
    void handlePost() throws Exception {
        CustomerDetailsDTO testCustomer = customersList.getFirst();
        CustomerCreationRequestDTO request = new CustomerCreationRequestDTO(testCustomer.name());

        when(customerService.saveNewCustomer(any(CustomerCreationRequestDTO.class)))
                .thenReturn(testCustomer);

        mockMvc.perform(
            post(CustomerController.CUSTOMER_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"))
            .andExpect(header().string("Location", "/api/v1/customers/" + testCustomer.id()));
    }

    @Test
    void testUpdateCustomer() throws Exception {
        CustomerDetailsDTO testCustomer = customersList.getFirst();
        CustomerUpdateRequestDTO customerUpdateRequestDTO = new CustomerUpdateRequestDTO(testCustomer.name());
        mockMvc.perform(
            put(CustomerController.CUSTOMER_PATH_ID, testCustomer.id())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customerUpdateRequestDTO))
        )
            .andExpect(status().isNoContent());

        verify(customerService, times(1))
            .updateCustomerById(eq(testCustomer.id()), customerUpdateRequestArgumentCaptor.capture());

        CustomerUpdateRequestDTO capturedRequest = customerUpdateRequestArgumentCaptor.getValue();
        assertThat(capturedRequest.name()).isEqualTo(testCustomer.name());
    }

    @Test
    void testDeleteCustomer() throws Exception {
        CustomerDetailsDTO testCustomer = customersList.getFirst();
        mockMvc.perform(
            delete(CustomerController.CUSTOMER_PATH_ID, testCustomer.id())
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isNoContent());

        verify(customerService, times(1))
            .deleteCustomerById(eq(testCustomer.id()));
    }
}