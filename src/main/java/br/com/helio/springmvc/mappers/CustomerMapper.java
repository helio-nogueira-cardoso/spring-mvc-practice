package br.com.helio.springmvc.mappers;

import br.com.helio.springmvc.dto.customer.CustomerCreationRequestDTO;
import br.com.helio.springmvc.dto.customer.CustomerDetailsDTO;
import br.com.helio.springmvc.dto.customer.CustomerUpdateRequestDTO;
import br.com.helio.springmvc.entities.Customer;
import org.mapstruct.Mapper;

@Mapper
public interface CustomerMapper {
    CustomerDetailsDTO customerToCustomerDetailsDto(Customer customer);
    CustomerCreationRequestDTO customerUpdateRequestDtoToCustomerCreationRequestDto(CustomerUpdateRequestDTO dto);
}
