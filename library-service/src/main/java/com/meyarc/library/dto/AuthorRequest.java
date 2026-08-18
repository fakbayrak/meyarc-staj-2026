package com.meyarc.library.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthorRequest {

    @NotBlank(message = "name bos olamaz")
    private String name;
}
