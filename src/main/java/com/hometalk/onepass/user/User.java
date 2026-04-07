package com.hometalk.onepass.user;

import jakarta.persistence.*;
import lombok.Getter;



@Entity(name = "user") // 다른 이름으로 구분
@Getter
@Table(name = "kjh_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
}
