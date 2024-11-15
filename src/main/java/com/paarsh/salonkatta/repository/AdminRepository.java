package com.paarsh.salonkatta.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.paarsh.salonkatta.model.Admin;

public interface AdminRepository extends JpaRepository<Admin, Long> {

    
}
