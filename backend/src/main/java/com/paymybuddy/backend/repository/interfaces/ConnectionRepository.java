package com.paymybuddy.backend.repository.interfaces;

import com.paymybuddy.backend.model.entity.Connection;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConnectionRepository extends JpaRepository<Connection,Integer> {
}
