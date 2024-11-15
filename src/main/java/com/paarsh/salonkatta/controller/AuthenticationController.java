package com.paarsh.salonkatta.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.paarsh.salonkatta.dto.AuthenticationRequest;
import com.paarsh.salonkatta.serviceImpl.JwtService;
import com.paarsh.salonkatta.serviceImpl.UserDetailsServiceImpl;
import com.paarsh.salonkatta.serviceImpl.JwtService;

import com.paarsh.salonkatta.dto.AuthenticationRequest;
import com.paarsh.salonkatta.dto.JwtResponse;
import com.paarsh.salonkatta.dto.UserDto;
import com.paarsh.salonkatta.model.Admin;
import com.paarsh.salonkatta.model.Customer;
import com.paarsh.salonkatta.model.Role;
import com.paarsh.salonkatta.model.User;
import com.paarsh.salonkatta.model.Vendor;
import com.paarsh.salonkatta.serviceImpl.JwtService;

@RestController
@Slf4j
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsServiceImpl userService;

    @PostMapping("/authenticate")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest)
            throws Exception {
        authenticate(authenticationRequest.getEmail(), authenticationRequest.getPassword());

        final UserDetails userDetails = userService.loadUserByUsername(authenticationRequest.getEmail());
        final String token = jwtService.generateToken(userDetails.getUsername());

        return ResponseEntity.ok(new JwtResponse(token));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserDto userDto) {

        // Determine if the authenticated user is allowed to register the requested role
        Role requestedRole = userDto.getRole();

        // if (!isSignupAllowed(requestedRole)) {
        //     return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You don't have permission to sign up this role.");
        // }

        // Check if the email is already taken
        if (userService.userExists(userDto.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email is already taken.");
        }

        // Save the user and generate a token
        User user = userService.saveUser(userDto);
        String token = jwtService.generateToken(user.getEmail());
        // Handle role-specific logic
        switch (requestedRole) {
            case CUSTOMER:
                Customer createdCustomer = userService.addUserInCustomer(user);
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body("Retailer created with id " + createdCustomer.getId() + " token is " + token);

            case VENDOR:
                Vendor createdVendor = userService.addUserInVendor(user);
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body("Vendor created with id " + createdVendor.getId() + " token is " + token);
            case ADMIN:

                Admin createdAdmin = userService.addUserInAdmin(user);
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body("Admin created with id " + createdAdmin.getId() + " token is " + token);
            

            default:
                return ResponseEntity.badRequest().body("Invalid role.");
        }

    }

    private void authenticate(String email, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }

    private boolean isSignupAllowed(Role requestedRole) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails currentUser = (UserDetails) authentication.getPrincipal();
        String currentUserRole = userService.findByEmail(currentUser.getUsername()).get().getRole().name();
        log.info("Currunt user role is " + currentUserRole);
        return switch (currentUserRole) {
            case "SUPERADMIN" ->
                // SuperAdmin can sign up VENDOR,Admin,Customer 
                requestedRole == Role.ADMIN || requestedRole == Role.CUSTOMER || requestedRole == Role.VENDOR ;
            case "ADMIN" ->
                // Admin can sign up vendor
                requestedRole == Role.VENDOR;
            case "VENDOR" ->
                false;
            case "CUSTOMER" ->
                // Retailer can create a customer, but customers do not require signup in this
                // flow
                false;
            default -> false;
        };
    }
}
