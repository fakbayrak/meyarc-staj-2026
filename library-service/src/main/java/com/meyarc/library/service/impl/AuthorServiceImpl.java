package com.meyarc.library.service.impl;

import com.meyarc.library.dto.AuthorRequest;
import com.meyarc.library.dto.AuthorResponse;
import com.meyarc.library.entity.Author;
import com.meyarc.library.exception.ResourceNotFoundException;
import com.meyarc.library.mapper.AuthorMapper;
import com.meyarc.library.repository.AuthorRepository;
import com.meyarc.library.service.AuthorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;
    private final AuthorMapper authorMapper;

    @Override
    public AuthorResponse create(AuthorRequest request) {
        Author author = authorMapper.toEntity(request);
        Author saved = authorRepository.save(author);
        return authorMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public AuthorResponse getById(Long id) {
        Author author = findAuthorOrThrow(id);
        return authorMapper.toResponse(author);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuthorResponse> getAll(Pageable pageable) {
        return authorRepository.findAll(pageable)
                .map(authorMapper::toResponse);
    }

    @Override
    public AuthorResponse update(Long id, AuthorRequest request) {
        Author author = findAuthorOrThrow(id);
        author.setName(request.getName());
        return authorMapper.toResponse(author);
    }

    @Override
    public void delete(Long id) {
        Author author = findAuthorOrThrow(id);
        authorRepository.delete(author);
    }

    private Author findAuthorOrThrow(Long id) {
        return authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Author bulunamadi, id=" + id));
    }
}
