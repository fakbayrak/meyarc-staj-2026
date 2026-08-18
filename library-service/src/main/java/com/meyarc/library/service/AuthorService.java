package com.meyarc.library.service;

import com.meyarc.library.dto.AuthorRequest;
import com.meyarc.library.dto.AuthorResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AuthorService {

    AuthorResponse create(AuthorRequest request);

    AuthorResponse getById(Long id);

    Page<AuthorResponse> getAll(Pageable pageable);

    AuthorResponse update(Long id, AuthorRequest request);

    void delete(Long id);
}
