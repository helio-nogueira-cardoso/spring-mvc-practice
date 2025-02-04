package br.com.helio.springmvc.mappers;

import br.com.helio.springmvc.dto.customer.CustomerDetailsDTO;
import br.com.helio.springmvc.entities.Customer;
import org.mapstruct.Mapper;

@Mapper
public interface CustomerMapper {
    Customer customerDetailsDtoToCustomer(CustomerDetailsDTO dto);
    CustomerDetailsDTO customerToCustomerDetailsDto(Customer customer);
}
