package com.paarsh.salonkatta.dto;


import com.paarsh.salonkatta.model.Role;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {
    private String Email;
    private String password;
    private Role role;

}

