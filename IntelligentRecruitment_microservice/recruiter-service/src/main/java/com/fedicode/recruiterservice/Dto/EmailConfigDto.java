package com.fedicode.recruiterservice.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailConfigDto {
    private String acceptSubject;
    private String acceptBody;
    private String refuseSubject;
    private String refuseBody;
}