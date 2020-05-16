package de.brightside.bnotes.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import de.brightside.bnotes.model.Role;

public interface RoleRepo extends JpaRepository<Role, Long> {
    Optional<Role> findByRoleName(String name);
}

