package dev.cretara.customer.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("customers")
public class CustomerController {

    @GetMapping("{id}")
    public String getCustomerById() {
        return "Hello World";
    }

}
