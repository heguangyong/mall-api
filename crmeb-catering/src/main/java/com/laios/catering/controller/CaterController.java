package com.laios.catering.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.PermitAll;

@RestController
@RequestMapping("/cater")
public class CaterController {

    @PermitAll
    @GetMapping("/cater")
    public String hw(){
        return "hello world";
    }

}
