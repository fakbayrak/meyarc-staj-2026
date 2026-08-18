package com.meyarc.library.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class BookRequest {

    @NotBlank(message = "title bos olamaz")
    private String title;

    @NotBlank(message = "isbn bos olamaz")
    private String isbn;

    @Min(value = 0, message = "publicationYear negatif olamaz")
    private Integer publicationYear;

    private Long authorId;

    private Set<Long> categoryIds;
}
