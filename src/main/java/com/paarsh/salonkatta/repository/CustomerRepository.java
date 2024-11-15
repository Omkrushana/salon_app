package com.paarsh.salonkatta.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.paarsh.salonkatta.model.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

}
