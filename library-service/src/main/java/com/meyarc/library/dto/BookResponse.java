package com.meyarc.library.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@Getter
@AllArgsConstructor
public class BookResponse {

    private Long id;
    private String title;
    private String isbn;
    private Integer publicationYear;
    private String authorName;
    private Set<String> categoryNames;
}
