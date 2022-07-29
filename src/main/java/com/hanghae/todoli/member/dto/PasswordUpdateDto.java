package com.hanghae.todoli.member.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PasswordUpdateDto {
    private String curPassword;
    private String changePassword;
}
