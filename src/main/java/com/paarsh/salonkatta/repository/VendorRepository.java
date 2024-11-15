package com.paarsh.salonkatta.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.paarsh.salonkatta.model.Vendor;

public interface VendorRepository extends JpaRepository<Vendor, Long> {

    
}
