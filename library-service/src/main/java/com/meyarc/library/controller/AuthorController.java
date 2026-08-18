package com.meyarc.library.controller;

import com.meyarc.library.dto.AuthorRequest;
import com.meyarc.library.dto.AuthorResponse;
import com.meyarc.library.service.AuthorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/authors")
@RequiredArgsConstructor
public class AuthorController {

    private final AuthorService authorService;

    @PostMapping
    public ResponseEntity<AuthorResponse> create(@Valid @RequestBody AuthorRequest request) {
        AuthorResponse created = authorService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public AuthorResponse getById(@PathVariable Long id) {
        return authorService.getById(id);
    }

    @GetMapping
    public Page<AuthorResponse> getAll(Pageable pageable) {
        return authorService.getAll(pageable);
    }

    @PutMapping("/{id}")
    public AuthorResponse update(@PathVariable Long id, @Valid @RequestBody AuthorRequest request) {
        return authorService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        authorService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
