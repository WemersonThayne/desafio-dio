package com.example.demo.service;

import com.example.demo.api_gateway.ViaCepService;
import com.example.demo.domain.Customer;
import com.example.demo.exceptions.CustomerAlreadyExistsException;
import com.example.demo.exceptions.CustomerNotFoundException;
import com.example.demo.repository.AddressRepository;
import com.example.demo.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository repository;
    private final AddressRepository addressRepository;
    private final ViaCepService viaCepService;

    public List<Customer> findAll(){
       log.info("Get all customers....");
       return repository.findAll();
    }

    public Customer findById(Long id) {
        return repository.findById(id)
                .orElseThrow( () -> new CustomerNotFoundException("Customer not found for this id %s".formatted(id)));
    }

    public Customer save(Customer customer){

        if(repository.existsByEmail(customer.getEmail())){
            log.warn("Customer {} already exists in database",customer.getEmail());
            throw new CustomerAlreadyExistsException("Customer %s already exists.".formatted(customer.getEmail()));
        }

        log.info("Saving new Customer in database.");
        var cep = customer.getAddress().getCep();
        var address = addressRepository.findById(cep).orElseGet( () ->
            addressRepository.save(viaCepService.getCep(cep))
        );
        customer.saveAdd(address);
        return repository.save(customer);

    }

}
