package com.hometalk.onepass.survey.repository;

import com.hometalk.onepass.survey.entity.Survey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SurveyRepository extends JpaRepository<Survey, Long> {
}