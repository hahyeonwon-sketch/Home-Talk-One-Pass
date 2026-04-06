package com.hometalk.onepass.common.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;


@Entity
@Data
public class User extends CommonEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long householdId;
    private String loginId;
    private String name;
    private String nickname;
    private String email;
    private String phoneNumber;
    private String status;
    private String role;
}
