package com.meyarc.library.service;

import com.meyarc.library.dto.BookRequest;
import com.meyarc.library.dto.BookResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookService {

    BookResponse create(BookRequest request);

    BookResponse getById(Long id);

    Page<BookResponse> getAll(Pageable pageable);

    BookResponse update(Long id, BookRequest request);

    void delete(Long id);
}
