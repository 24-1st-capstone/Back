package com.capstone.usa.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModifyUserDto {
    private String name;
    private String Password;
    private String currentPassword;
}
