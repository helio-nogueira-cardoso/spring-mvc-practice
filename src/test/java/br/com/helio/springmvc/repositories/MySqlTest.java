package br.com.helio.springmvc.repositories;

import br.com.helio.springmvc.entities.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest
@ActiveProfiles("localmysql")
public class MySqlTest {
    @Container
    static MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:9");

    @DynamicPropertySource
    static void mySqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.username", mySQLContainer::getUsername);
        registry.add("spring.datasource.password", mySQLContainer::getPassword);
        registry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
    }

//    @Autowired
//    DataSource dataSource;

    @Autowired
    CustomerRepository customerRepository;

    @Test
    void testListCustomers() {
        List<Customer> customers = customerRepository.findAll();

        assertThat(customers.size()).isGreaterThan(0);
    }
}
