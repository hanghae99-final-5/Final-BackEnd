package com.hanghae.todoli;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class hiController {
    @GetMapping("/")
    public String abc(){
        return "home";
    }
}
