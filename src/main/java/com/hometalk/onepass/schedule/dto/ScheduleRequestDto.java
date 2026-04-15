package com.hometalk.onepass.schedule.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleRequestDto {

    @NotBlank
    private String userId;

    @NotBlank
    private String title;


}
