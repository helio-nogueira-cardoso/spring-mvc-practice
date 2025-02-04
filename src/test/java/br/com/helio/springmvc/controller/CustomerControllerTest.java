package br.com.helio.springmvc.controller;

import br.com.helio.springmvc.dto.customer.CustomerDetails;
import br.com.helio.springmvc.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.core.Is.is;

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    CustomerService customerService;

    @Test
    void getCustomerById() throws Exception {
        CustomerDetails testCustomerDetails = new CustomerDetails(
            UUID.randomUUID(),
            "Test Customer",
            1,
            LocalDateTime.now(),
            LocalDateTime.now()
        );

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
}