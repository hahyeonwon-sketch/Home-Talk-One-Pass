package com.hometalk.onepass.survey.entity;

import com.hometalk.onepass.common.entity.CommonEntity;
import jakarta.persistence.*;
import lombok.Data;


@Entity
@Data
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"survey_id", "display_order"}))
@AttributeOverrides({
        @AttributeOverride(name = "content", column = @Column(nullable = false))
})
public class Question extends CommonEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long surveyId;
    private int displayOrder;
    @Column(columnDefinition = "boolean default true")
    private Boolean isRequired;
}