package com.hometalk.onepass.notice.entity;


import com.hometalk.onepass.common.entity.CommonEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "")
public class Attachment extends CommonEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String targetType;
    private Long targetId;
    private String fileName;
    private String filePath;
    private int fileSize;
}
