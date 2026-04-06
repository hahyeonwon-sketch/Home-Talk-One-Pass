package com.hometalk.onepass.schedule.entity;

import com.hometalk.onepass.common.entity.CommonEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@AttributeOverrides({
        @AttributeOverride(name = "title", column = @Column(nullable = false))
})
public class Schedule extends CommonEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private String targetType;
    private Long targetId;
    private String info;
    private String location;
    private String referenceUrl;
    private LocalDateTime startAt;
    private LocalDateTime endAt;

}
