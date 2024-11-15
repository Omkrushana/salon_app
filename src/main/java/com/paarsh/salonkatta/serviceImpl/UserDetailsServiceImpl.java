package com.paarsh.salonkatta.serviceImpl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.paarsh.salonkatta.dto.UserDto;
import com.paarsh.salonkatta.model.Admin;
import com.paarsh.salonkatta.model.Customer;
import com.paarsh.salonkatta.model.Role;
import com.paarsh.salonkatta.model.User;
import com.paarsh.salonkatta.model.Vendor;
import com.paarsh.salonkatta.repository.AdminRepository;
import com.paarsh.salonkatta.repository.CustomerRepository;
import com.paarsh.salonkatta.repository.UserRepository;
import com.paarsh.salonkatta.repository.VendorRepository;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private VendorRepository vendorRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByEmail(username);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();

        // role assignments done in below code
        switch (user.get().getRole()) {
            case SUPERADMIN:
                grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_SUPERADMIN"));
                break;
            case CUSTOMER:
                grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_CUSTOMER"));
                break;
            case VENDOR:
                grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_VENDOR"));
                break;
            case ADMIN:
                grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                break;
            default:
                grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_CUSTOMER"));
        }
        return new User(user.get().getEmail(),
                user.get().getPassword(),
                grantedAuthorities);
    }

    public boolean userExists(String username) {
        return userRepository.findByEmail(username).isPresent();
    }

    public User saveUser(UserDto userDto) {
        User user = new User();
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setRole(userDto.getRole());
        return userRepository.save(user);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Customer createCustomer(Customer customer, String email, String password) {
        // Create User with encoded password
        User user = new User(email, passwordEncoder.encode(password),
                Collections.singletonList(new SimpleGrantedAuthority("CUSTOMER")));
        user.setRole(Role.CUSTOMER);

        // Save User
        User savedUser = userRepository.save(user);

        // Associate User with Customer
        customer.setUser(savedUser);
        return customerRepository.save(customer);
    }

    public Admin createAdmin(Admin admin, String email, String password) {
        // Create User with encoded password
        User user = new User(email, passwordEncoder.encode(password),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_RETAILER")));
        user.setRole(Role.ADMIN);

        // Save User
        User savedUser = userRepository.save(user);

        // Associate User with Admin
        admin.setUser(savedUser);
        return adminRepository.save(admin);
    }

    public Vendor createVendor(Vendor vendor, String email, String password) {
        // Create User with encoded password
        User user = new User(email, passwordEncoder.encode(password),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_VENDOR")));
        user.setRole(Role.VENDOR);

        // Save User
        User savedUser = userRepository.save(user);

        // Associate User with VENDOR
        vendor.setUser(savedUser);
        return vendorRepository.save(vendor);
    }

    public Customer addUserInCustomer(User user) {
        Customer customer = new Customer();
        customer.setUser(user);
        return customerRepository.save(customer);
    }

    public Vendor addUserInVendor(User user) {
        Vendor vendor = new Vendor();
        vendor.setUser(user);
        return vendorRepository.save(vendor);
    }

    public Admin addUserInAdmin(User user) {
        Admin admin = new Admin();
        admin.setUser(user);
        return adminRepository.save(admin);
    }

}
