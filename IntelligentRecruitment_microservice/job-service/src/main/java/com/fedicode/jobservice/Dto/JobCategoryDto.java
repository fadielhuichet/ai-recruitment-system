package com.fedicode.jobservice.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;

// JobCategoryDto.java
@Data
@AllArgsConstructor
public class JobCategoryDto {
    private String value;
    private String label;
    private String group;
}