package com.hometalk.onepass.survey.entity;

import com.hometalk.onepass.common.entity.CommonEntity;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"household_id", "question_id"}))
public class Response extends CommonEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long householdId;
    private Long questionId;
    private Long optionId;
}