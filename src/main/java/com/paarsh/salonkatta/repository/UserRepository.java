package com.paarsh.salonkatta.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.paarsh.salonkatta.model.User;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

}