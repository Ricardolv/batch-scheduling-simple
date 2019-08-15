package com.richard.repositories;

import com.richard.model.Voltage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoltageRepository extends JpaRepository<Voltage, Long> {

}
