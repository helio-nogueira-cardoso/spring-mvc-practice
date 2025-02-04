package br.com.helio.springmvc.controller;

import br.com.helio.springmvc.dto.customer.CustomerCreationRequest;
import br.com.helio.springmvc.dto.customer.CustomerDetails;
import br.com.helio.springmvc.service.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

    private final List<CustomerDetails> customersList = new ArrayList<>(List.of(
        new CustomerDetails(
                UUID.randomUUID(),
                "Test Customer 1",
                1,
                LocalDateTime.now(),
                LocalDateTime.now()
        ),
        new CustomerDetails(
                UUID.randomUUID(),
                "Test Customer 2",
                1,
                LocalDateTime.now(),
                LocalDateTime.now()
        ),
        new CustomerDetails(
                UUID.randomUUID(),
                "Test Customer 3",
                1,
                LocalDateTime.now(),
                LocalDateTime.now()
        )
    ));

    @Test
    void getCustomerById() throws Exception {
        CustomerDetails testCustomerDetails = customersList.getFirst();

        when(customerService.getCustomerDetaisById(testCustomerDetails.id()))
                .thenReturn(testCustomerDetails);

        mockMvc
            .perform(
                get("/api/v1/customers/" + testCustomerDetails.id())
                    .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id",  is(testCustomerDetails.id().toString())))
            .andExpect(jsonPath("$.name", is(testCustomerDetails.name())));
    }

    @Test
    void listCustomers() throws Exception {
        when(customerService.listCustomers())
                .thenReturn(customersList);

        mockMvc
            .perform(
                get("/api/v1/customers")
                        .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.length()", is(customersList.size())));
    }

    @Test
    void handlePost() throws Exception {
        CustomerDetails testCustomer = customersList.getFirst();
        CustomerCreationRequest request = new CustomerCreationRequest(testCustomer.name());

        when(customerService.saveNewCustomer(any(CustomerCreationRequest.class)))
                .thenReturn(testCustomer);

        mockMvc.perform(
                post("/api/v1/customers")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"))
            .andExpect(header().string("Location", "/api/v1/customers/" + testCustomer.id()));
    }
}