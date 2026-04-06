package com.hometalk.onepass.survey.entity;

import com.hometalk.onepass.common.entity.CommonEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@AttributeOverrides({
        @AttributeOverride(name = "title", column = @Column(nullable = false))
})
public class Survey extends CommonEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int viewCount;
    @Column(columnDefinition = "boolean default false")
    private Boolean isAnonymous;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
}