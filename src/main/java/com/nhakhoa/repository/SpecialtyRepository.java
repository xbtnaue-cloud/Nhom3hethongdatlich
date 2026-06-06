package com.nhakhoa.repository;

import com.nhakhoa.model.Specialty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpecialtyRepository extends JpaRepository<Specialty, Integer> {
    // Không cần viết code, JPA đã có sẵn hàm findAll()
}