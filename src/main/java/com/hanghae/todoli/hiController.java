package com.hanghae.todoli;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class hiController {
    @GetMapping("/health")
    public String abc(){
        return "qqqq";
    }
}
