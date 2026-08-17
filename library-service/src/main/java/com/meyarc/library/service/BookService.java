package com.meyarc.library.service;

import com.meyarc.library.dto.BookRequest;
import com.meyarc.library.dto.BookResponse;

import java.util.List;

public interface BookService {

    BookResponse create(BookRequest request);

    BookResponse getById(Long id);

    List<BookResponse> getAll();

    BookResponse update(Long id, BookRequest request);

    void delete(Long id);
}
