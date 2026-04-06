package com.hometalk.onepass.survey.repository;

import com.hometalk.onepass.survey.entity.Response;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResponseRepository extends JpaRepository<Response, Long> {
}