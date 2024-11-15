package com.paarsh.salonkatta.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
public class JwtResponse {
    private final String token;
    // private final String role;
    // public JwtResponse(String token, String role) {
    //     this.token = token;
    //     this.role = role;
    // }
}
