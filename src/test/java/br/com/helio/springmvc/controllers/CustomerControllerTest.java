package br.com.helio.springmvc.controllers;

import br.com.helio.springmvc.config.SpringSecurityConfiguration;
import br.com.helio.springmvc.dto.customer.CustomerCreationRequestDTO;
import br.com.helio.springmvc.dto.customer.CustomerDetailsDTO;
import br.com.helio.springmvc.dto.customer.CustomerUpdateRequestDTO;
import br.com.helio.springmvc.services.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.core.Is.is;

@WebMvcTest(CustomerController.class)
@Import(SpringSecurityConfiguration.class)
@ActiveProfiles("localmysql")
class CustomerControllerTest {
    @Value("${spring.security.user.name}")
    String username;

    @Value("${spring.security.user.password}")
    String password;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    CustomerService customerService;

    @Captor
    ArgumentCaptor<CustomerUpdateRequestDTO> customerUpdateRequestArgumentCaptor;

    private HttpHeaders getAuthHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(username, password);
        return headers;
    }

    private final List<CustomerDetailsDTO> customersList = new ArrayList<>(List.of(
        new CustomerDetailsDTO(
                UUID.randomUUID(),
                "Test Customer 1",
                "customer1@mail.com",
                1,
                LocalDateTime.now(),
                LocalDateTime.now()
        ),
        new CustomerDetailsDTO(
                UUID.randomUUID(),
                "Test Customer 2",
                "customer2@mail.com",
                1,
                LocalDateTime.now(),
                LocalDateTime.now()
        ),
        new CustomerDetailsDTO(
                UUID.randomUUID(),
                "Test Customer 3",
                "customer3@mail.com",
                1,
                LocalDateTime.now(),
                LocalDateTime.now()
        )
    ));

    @Test
    void getCustomerById() throws Exception {
        CustomerDetailsDTO testCustomerDetailsDTO = customersList.getFirst();

        when(customerService.getCustomerDetailsById(testCustomerDetailsDTO.id()))
                .thenReturn(Optional.of(testCustomerDetailsDTO));

        mockMvc
            .perform(
                get(CustomerController.CUSTOMER_PATH_ID, testCustomerDetailsDTO.id())
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(getAuthHeader())
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id",  is(testCustomerDetailsDTO.id().toString())))
            .andExpect(jsonPath("$.name", is(testCustomerDetailsDTO.name())))
            .andExpect(jsonPath("$.email", is(testCustomerDetailsDTO.email())));
    }

    @Test
    void failingAuthenticationGetCustomerById() throws Exception {
        CustomerDetailsDTO testCustomerDetailsDTO = customersList.getFirst();

        when(customerService.getCustomerDetailsById(testCustomerDetailsDTO.id()))
                .thenReturn(Optional.of(testCustomerDetailsDTO));

        mockMvc
                .perform(
                        get(CustomerController.CUSTOMER_PATH_ID, testCustomerDetailsDTO.id())
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getCustomerByIdNotFound() throws Exception {
        when(customerService.getCustomerDetailsById(any(UUID.class)))
                .thenReturn(Optional.empty());

        mockMvc
            .perform(
                get(CustomerController.CUSTOMER_PATH_ID, UUID.randomUUID())
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(getAuthHeader())
            )
            .andExpect(status().isNotFound());
    }

    @Test
    void listCustomers() throws Exception {
        when(customerService.listCustomers(any(), any(), any()))
                .thenReturn(new PageImpl<>(customersList));

        mockMvc
            .perform(
                get(CustomerController.CUSTOMER_PATH)
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(getAuthHeader())
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.content.length()", is(customersList.size())));
    }

    @Test
    void saveNewCustomer() throws Exception {
        CustomerDetailsDTO testCustomer = customersList.getFirst();
        CustomerCreationRequestDTO request = new CustomerCreationRequestDTO(testCustomer.name(), testCustomer.email());

        when(customerService.saveNewCustomer(any(CustomerCreationRequestDTO.class)))
                .thenReturn(testCustomer);

        mockMvc.perform(
            post(CustomerController.CUSTOMER_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .headers(getAuthHeader())
        )
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"))
            .andExpect(
                header().string("Location",
                    UriComponentsBuilder
                            .fromPath(CustomerController.CUSTOMER_PATH_ID)
                            .buildAndExpand(testCustomer.id())
                            .toUriString()
                )
            );
    }

    @Test
    void testUpdateExistingCustomer() throws Exception {
        // Arrange
        final String NEW_NAME = "UPDATED";
        final String NEW_EMAIL = "newemail@mail.com";
        CustomerDetailsDTO testCustomer = customersList.getFirst();
        CustomerDetailsDTO expectedUpdatedCustomer = CustomerDetailsDTO.builder()
                .id(testCustomer.id())
                .name(NEW_NAME)
                .email(NEW_EMAIL)
                .version(testCustomer.version())
                .createdDate(testCustomer.createdDate())
                .lastModifiedDate(testCustomer.lastModifiedDate())
                .build();

        when(customerService.updateCustomerById(eq(testCustomer.id()), any(CustomerUpdateRequestDTO.class)))
                .thenReturn(expectedUpdatedCustomer);

        // Act
        CustomerUpdateRequestDTO customerUpdateRequestDTO = new CustomerUpdateRequestDTO(NEW_NAME, NEW_EMAIL);
        mockMvc.perform(
            put(CustomerController.CUSTOMER_PATH_ID, testCustomer.id())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customerUpdateRequestDTO))
                .headers(getAuthHeader())
        )
            .andExpect(status().isNoContent())
            .andExpect(header().doesNotExist("Location"));

        verify(customerService, times(1))
            .updateCustomerById(eq(testCustomer.id()), customerUpdateRequestArgumentCaptor.capture());

        CustomerUpdateRequestDTO capturedRequest = customerUpdateRequestArgumentCaptor.getValue();
        assertThat(capturedRequest.name()).isEqualTo(NEW_NAME);
        assertThat(capturedRequest.email()).isEqualTo(NEW_EMAIL);
    }

    @Test
    void testUpdateAbsentCustomer() throws Exception {
        // Arrange
        final String NEW_NAME = "UPDATED";
        final String NEW_EMAIL = "newemail@mail.com";
        final UUID NEW_CUSTOMER_UUID = UUID.randomUUID();

        CustomerDetailsDTO testCustomer = customersList.getFirst();
        CustomerDetailsDTO expectedNewCustomer = CustomerDetailsDTO.builder()
                .id(NEW_CUSTOMER_UUID)
                .name(NEW_NAME)
                .email(NEW_EMAIL)
                .version(1)
                .createdDate(LocalDateTime.now())
                .lastModifiedDate(LocalDateTime.now())
                .build();

        when(customerService.updateCustomerById(eq(testCustomer.id()), any(CustomerUpdateRequestDTO.class)))
                .thenReturn(expectedNewCustomer);

        // Act
        CustomerUpdateRequestDTO customerUpdateRequestDTO = new CustomerUpdateRequestDTO(NEW_NAME, NEW_EMAIL);
        mockMvc.perform(
                        put(CustomerController.CUSTOMER_PATH_ID, testCustomer.id())
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(customerUpdateRequestDTO))
                                .with(httpBasic(username, password))
                )
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(
                        header().string("Location",
                                UriComponentsBuilder
                                        .fromPath(CustomerController.CUSTOMER_PATH_ID)
                                        .buildAndExpand(NEW_CUSTOMER_UUID)
                                        .toUriString()
                        )
                );

        verify(customerService, times(1))
                .updateCustomerById(eq(testCustomer.id()), customerUpdateRequestArgumentCaptor.capture());

        CustomerUpdateRequestDTO capturedRequest = customerUpdateRequestArgumentCaptor.getValue();
        assertThat(capturedRequest.name()).isEqualTo(NEW_NAME);
        assertThat(capturedRequest.email()).isEqualTo(NEW_EMAIL);
    }

    @Test
    void testDeleteCustomer() throws Exception {
        CustomerDetailsDTO testCustomer = customersList.getFirst();
        final UUID ID_TO_BE_DELETED = testCustomer.id();
        when(customerService.deleteCustomerById(eq(ID_TO_BE_DELETED)))
                .thenReturn(true);

        mockMvc.perform(
            delete(CustomerController.CUSTOMER_PATH_ID, ID_TO_BE_DELETED)
                .accept(MediaType.APPLICATION_JSON)
                .headers(getAuthHeader())
        )
            .andExpect(status().isNoContent());

        verify(customerService, times(1))
            .deleteCustomerById(eq(ID_TO_BE_DELETED));
    }

    @Test
    void testDeleteCustomerNotFound() throws Exception {
        final UUID ABSENT_ID = UUID.randomUUID();
        when(customerService.deleteCustomerById(eq(ABSENT_ID)))
                .thenReturn(false);

        mockMvc.perform(
                        delete(CustomerController.CUSTOMER_PATH_ID, ABSENT_ID)
                                .accept(MediaType.APPLICATION_JSON)
                                .headers(getAuthHeader())
                )
                .andExpect(status().isNotFound());

        verify(customerService, times(1))
                .deleteCustomerById(eq(ABSENT_ID));
    }

    @Test
    void emptyCustomerShouldNotBeSaved() throws Exception {
        CustomerCreationRequestDTO emptyRequest = CustomerCreationRequestDTO.builder().build();
        when(customerService.saveNewCustomer(any(CustomerCreationRequestDTO.class)))
                .thenReturn(customersList.getFirst());

        MvcResult mvcResult = mockMvc.perform(
                        post(CustomerController.CUSTOMER_PATH)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(emptyRequest))
                                .headers(getAuthHeader())
                )
                .andExpect(status().isBadRequest()).andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        System.out.println(jsonResponse);

        List<String> names = JsonPath.read(jsonResponse, "$[*].name");
        boolean hasExpectedName = names.stream().anyMatch(name -> name.equals("must not be blank"));
        assertThat(hasExpectedName).isTrue();
    }
}