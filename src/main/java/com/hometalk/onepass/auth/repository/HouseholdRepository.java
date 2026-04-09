package com.hometalk.onepass.auth.repository;

import com.hometalk.onepass.auth.entity.Household;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HouseholdRepository extends JpaRepository<Household, Long> {
}