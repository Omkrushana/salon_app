package com.paarsh.salonkatta.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.paarsh.salonkatta.model.SuperAdmin;

public interface SuperAdminReposiory extends JpaRepository<SuperAdmin, Long> {
    
}
