package com.hometalk.onepass.survey.repository;

import com.hometalk.onepass.survey.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {
}