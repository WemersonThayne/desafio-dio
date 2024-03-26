package com.example.demo.controller;

import com.example.demo.domain.Customer;
import com.example.demo.service.CustomerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
@Api(value = "API Customers", description = "API for consumer customers resources")
public class CustomerController {

    private final CustomerService service;


    @GetMapping
    @ApiOperation(value = "Return all custumers in database", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE, response = Customer.class, responseContainer = "List")
    public ResponseEntity<List<Customer>> getAll() {
        return ResponseEntity.ok().body(service.findAll());
    }

    @PostMapping
    @ApiOperation(value = "Create new custumer", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE, response = Customer.class)
    public ResponseEntity<Customer> save(@RequestBody Customer customer){
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(customer));
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Return custumer away identifier", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE, response = Customer.class)
    public ResponseEntity<Customer> findById(@PathVariable("id") Long id){
        return ResponseEntity.ok().body(service.findById(id));
    }
}