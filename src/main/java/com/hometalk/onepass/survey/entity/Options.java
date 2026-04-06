package com.hometalk.onepass.survey.entity;

import com.hometalk.onepass.common.entity.CommonEntity;
import jakarta.persistence.*;
import lombok.Data;


@Entity
@Data
@AttributeOverrides({
        @AttributeOverride(name = "content", column = @Column(nullable = false))
})
public class Options extends CommonEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long questionId;
    private int optionOrder;
}
