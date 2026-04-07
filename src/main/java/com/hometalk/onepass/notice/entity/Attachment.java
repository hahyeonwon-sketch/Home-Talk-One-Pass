package com.hometalk.onepass.notice.entity;



import com.hometalk.onepass.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Attachment extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long noticeId;
    private String fileName;
    private String filePath;
    private int fileSize;
}
