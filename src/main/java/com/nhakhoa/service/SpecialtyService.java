package com.nhakhoa.service;

import com.nhakhoa.model.Specialty;
import com.nhakhoa.repository.SpecialtyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SpecialtyService {

    @Autowired
    private SpecialtyRepository specialtyRepo;

    public List<String> getAllSpecialties() {
        return specialtyRepo.findAll().stream()
                .map(Specialty::getSpecialtyName)
                .collect(Collectors.toList());
    }
}