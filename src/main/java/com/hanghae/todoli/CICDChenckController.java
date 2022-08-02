package com.hanghae.todoli;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CICDChenckController {
    @GetMapping("/health")
    public String abc(){
        return "진짜 축하합니다!";
    }
}
