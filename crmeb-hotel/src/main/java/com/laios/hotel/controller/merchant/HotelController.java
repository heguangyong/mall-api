package com.laios.hotel.controller.merchant;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.PermitAll;

@RestController
@RequestMapping("/hotel")
public class HotelController {

    @PermitAll
    @GetMapping("/hotel")
    public String hw(){
        return "hello world";
    }

}
