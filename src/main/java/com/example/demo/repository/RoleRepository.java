package com.example.demo.repository;

import com.example.demo.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long>
{
    RoleEntity findByName(String roleName);
}
