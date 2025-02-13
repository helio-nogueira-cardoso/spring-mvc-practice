package br.com.helio.springmvc.controllers;

import br.com.helio.springmvc.dto.customer.CustomerCreationRequestDTO;
import br.com.helio.springmvc.dto.customer.CustomerDetailsDTO;
import br.com.helio.springmvc.dto.customer.CustomerUpdateRequestDTO;
import br.com.helio.springmvc.entities.Customer;
import br.com.helio.springmvc.exceptions.NotFoundException;
import br.com.helio.springmvc.repositories.CustomerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import net.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("localmysql")
class CustomerControllerIT {
    @Value("${spring.security.user.name}")
    private String username;

    @Value("${spring.security.user.password}")
    private String password;

    @Autowired
    CustomerController customerController;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void testListCustomers() {
        long count = customerRepository.count();
        Page<CustomerDetailsDTO> dtos = customerController.listCustomers(null, 1, 25);
        assertThat(dtos.getContent().size()).isEqualTo(count);
    }

    @Test
    void testListCustomersByName() {
        Page<CustomerDetailsDTO> dtos = customerController.listCustomers("%lorem%", 1, 25);
        assertThat(dtos.getContent().size()).isEqualTo(2);
    }

    @Test
    @Transactional
    @Rollback
    void testEmptyList() {
        customerRepository.deleteAll();
        Page<CustomerDetailsDTO> dtos = customerController.listCustomers(null, 1, 25);
        assertThat(dtos.getContent().size()).isEqualTo(0);
    }

    @Test
    @Transactional
    @Rollback
    void testLimitOfPageRequest() {
        for (int i = 0; i < 1500; i++) {
            customerRepository.save(Customer.builder()
                        .name(RandomString.make(20))
                        .email(RandomString.make(10) + "@mail.com")
                    .build());
        }

        Page<CustomerDetailsDTO> customers = customerController.listCustomers(null, 1, 1500);
        assertThat(customers.getContent().size()).isEqualTo(1000);
    }

    @Test
    void testGetById() {
        Customer customer = customerRepository.findAll().getFirst();
        CustomerDetailsDTO dto = customerController.getCustomerById(customer.getId());
        assertThat(dto).isNotNull();
    }

    private UUID getNotPresentUUID() {
            UUID notPresentId;
            do {
                notPresentId = UUID.randomUUID();
            } while (customerRepository.findById(notPresentId).isPresent());

            return notPresentId;
    }

    @Test
    void testCustomerIdNotFound() {
        assertThrows(NotFoundException.class, () -> customerController.getCustomerById(getNotPresentUUID()));
    }

    private UUID getIdFromHeaders(HttpHeaders headers) {
        String[] locationUUID = Objects.requireNonNull(headers.getLocation())
                .getPath()
                .split("/");

        return UUID.fromString(locationUUID[4]);
    }

    @Test
    @Transactional
    @Rollback
    void testSaveNewCustomer(){
        CustomerCreationRequestDTO dto = CustomerCreationRequestDTO.builder()
                .name("New Customer")
                .build();

        ResponseEntity<HttpStatus> response = customerController.saveNewCustomer(dto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getHeaders().getLocation()).isNotNull();

        Optional<Customer> optionalCustomer = customerRepository.findById(getIdFromHeaders(response.getHeaders()));
        assertThat(optionalCustomer.isPresent()).isTrue();
    }

    @Test
    @Transactional
    @Rollback
    void updateExistingCustomer() {
        final String UPDATED_NAME = "UPDATED";
        CustomerUpdateRequestDTO updateRequestDto = CustomerUpdateRequestDTO.builder().name(UPDATED_NAME).build();

        Customer existingCustomer = customerRepository.findAll().getFirst();
        ResponseEntity<HttpStatus> response = customerController.updateById(existingCustomer.getId(), updateRequestDto);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        Optional<Customer> optionalCustomer = customerRepository.findById(existingCustomer.getId());
        assertThat(optionalCustomer.isPresent()).isTrue();

        Customer customer = optionalCustomer.orElse(null);
        assertThat(customer.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    @Rollback
    void updateNotExistingCustomer() {
        final String UPDATED_NAME = "UPDATED";
        final UUID ABSENT_ID = getNotPresentUUID();
        CustomerUpdateRequestDTO updateRequestDto = CustomerUpdateRequestDTO.builder().name(UPDATED_NAME).build();

        assertThat(customerRepository.existsById(ABSENT_ID)).isFalse();

        ResponseEntity<HttpStatus> response = customerController.updateById(ABSENT_ID, updateRequestDto);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getHeaders().getLocation()).isNotNull();

        assertThat(customerRepository.existsById(getIdFromHeaders(response.getHeaders()))).isTrue();
    }

    @Test
    @Transactional
    @Rollback
    void testDelete() {
        Customer existingCustomer = customerRepository.findAll().getFirst();
        final UUID ID_TO_BE_DELETED = existingCustomer.getId();

        ResponseEntity<HttpStatus> response = customerController.deleteById(ID_TO_BE_DELETED);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(customerRepository.findById(ID_TO_BE_DELETED).isEmpty()).isTrue();
    }

    @Test
    void testDeleteNotFound() {
        final UUID ABSENT_ID = getNotPresentUUID();
        assertThrows(NotFoundException.class, () -> customerController.deleteById(ABSENT_ID));
    }

    @Test
    /*
      Do not use @Transactional when you want to simulate an error comming
      from the database itself. Because the operations will be cached, and
      then not validated by the database.
     */
    void testSaveTooLongNameCustomer() throws Exception {
        MvcResult mvcResult = mockMvc.perform(
                        post(CustomerController.CUSTOMER_PATH)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Accept-Language", "en-US")
                                .content(
                                        objectMapper.writeValueAsString(CustomerCreationRequestDTO.builder()
                                                .name(RandomString.make(51))
                                                .build())
                                ).with(httpBasic(username, password))
                )
                .andExpect(status().isBadRequest())
                .andReturn();

        System.out.println(mvcResult.getResponse().getContentAsString());
    }
}