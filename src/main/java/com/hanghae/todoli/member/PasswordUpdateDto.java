package com.hanghae.todoli.member;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PasswordUpdateDto {
    private String curPassword;
    private String changePassword;
}
