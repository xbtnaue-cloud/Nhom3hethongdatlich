package com.nhakhoa.repository;

import com.nhakhoa.model.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Integer> {
    // Tự động có các hàm CRUD cơ bản (save, findById, delete, findAll...)
    List<Contact> findAllByOrderByCreatedAtDesc();
}
