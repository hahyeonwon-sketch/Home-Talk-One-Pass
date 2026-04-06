package com.hometalk.onepass.notice.entity;

import com.hometalk.onepass.common.entity.CommonEntity;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@AttributeOverrides({
        @AttributeOverride(name = "title", column = @Column(nullable = false)),
        @AttributeOverride(name = "content", column = @Column(nullable = false))
})
public class Notice extends CommonEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private boolean isPinned;
    private int viewCount;
    @Enumerated(EnumType.STRING)
    private Badge badge;
}
